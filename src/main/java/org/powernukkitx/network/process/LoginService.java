package org.powernukkitx.network.process;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.config.category.NetworkSettings;

import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Owns the threads and limits of the login sequence, so a burst of joins cannot reach the tick or
 * the world generation pool.
 * <p>
 * Login validation may block for a long time: the protocol library resolves the Minecraft auth
 * service over HTTPS the first time it is used and whenever its signing keys expire. It therefore
 * runs on a pool of its own instead of {@link org.powernukkitx.Server#getComputeThreadPool()}, which
 * the level tick waits on.
 * <p>
 * Player data reads and writes go through a single thread, so a read issued after a save always
 * sees that save.
 * <p>
 * It also keeps count of the player slots taken by logins that have been accepted but whose player
 * is not online yet, so the {@code max-players} limit holds while those logins finish.
 */
@Slf4j
@ApiStatus.Internal
public final class LoginService {
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30L;

    private final ThreadPoolExecutor validationExecutor;
    private final ExecutorService playerDataExecutor;
    private final AtomicInteger pendingLogins = new AtomicInteger();
    private final int maxPendingLogins;
    private final ConcurrentHashMap<InetAddress, Integer> pendingLoginsByAddress = new ConcurrentHashMap<>();
    private final int maxPendingLoginsPerAddress;
    private final AtomicInteger reservedPlayerSlots = new AtomicInteger();
    private final ArrayDeque<Waiter> waiting = new ArrayDeque<>();
    private final int maxWaitingLogins;

    /**
     * Outcome of {@link #admit(InetAddress, Runnable)}.
     */
    public enum Admission {
        /** A slot was free and has been taken. */
        ADMITTED,
        /** Every slot is taken; the login runs once one frees up. */
        QUEUED,
        /** Refused, because the queue is full or the address holds its share of slots already. */
        REFUSED
    }

    private record Waiter(@Nullable InetAddress address, Runnable resume) {
    }

    public LoginService(NetworkSettings settings) {
        final int processors = Runtime.getRuntime().availableProcessors();
        final int threads = settings.loginThreads() > 0 ? settings.loginThreads() : Math.max(2, processors / 2);
        this.maxPendingLogins = settings.maxPendingLogins() > 0 ? settings.maxPendingLogins() : Math.max(16, processors * 4);
        this.maxPendingLoginsPerAddress = settings.maxPendingLoginsPerAddress();
        this.maxWaitingLogins = settings.maxQueuedLogins() > 0 ? settings.maxQueuedLogins() : this.maxPendingLogins * 8;

        // Every admitted login has at most one validation step queued at a time
        this.validationExecutor = new ThreadPoolExecutor(threads, threads, 30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(this.maxPendingLogins),
            new ThreadFactoryBuilder().setNameFormat("Login Validator #%d").setDaemon(true).build());
        this.validationExecutor.allowCoreThreadTimeOut(true);
        this.playerDataExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("Player Data IO").setDaemon(true).build());
    }

    /**
     * Initialises the protocol library's authentication support in the background, so the first
     * players to join after startup do not wait for its HTTPS lookups.
     */
    public void warmUp() {
        Thread thread = new Thread(() -> {
            try {
                EncryptionUtils.getMojangPublicKey();
            } catch (Throwable t) {
                log.warn("Unable to initialise login authentication, logins will fail until the Minecraft "
                    + "auth service is reachable and the server is restarted", t);
            }
        }, "Login Warmup");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Reserves a slot for a login in progress.
     * <p>
     * Besides the global limit, a public address may only hold
     * {@code network-settings.maxPendingLoginsPerAddress} slots, so a single host cannot keep every
     * slot busy with logins that never verify. Loopback, site-local and link-local addresses are
     * exempt, since a proxy in front of the server presents all of its players from one of those.
     *
     * @param address the address the login comes from, or {@code null} when unknown
     * @return {@code false} when the global or the per-address limit is reached
     */
    public boolean tryAdmit(@Nullable InetAddress address) {
        final InetAddress limited = this.limitedAddress(address);
        if (limited != null) {
            final boolean[] admitted = new boolean[1];
            this.pendingLoginsByAddress.compute(limited, (_, count) -> {
                final int current = count != null ? count : 0;
                if (current >= this.maxPendingLoginsPerAddress) {
                    return count;
                }
                admitted[0] = true;
                return current + 1;
            });
            if (!admitted[0]) {
                return false;
            }
        }
        while (true) {
            int current = this.pendingLogins.get();
            if (current >= this.maxPendingLogins) {
                if (limited != null) {
                    this.releaseAddress(limited);
                }
                return false;
            }
            if (this.pendingLogins.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    /**
     * Reserves a slot for a login in progress, or queues the login until one frees up.
     * <p>
     * A burst of joins larger than {@link #getMaxPendingLogins()} waits for its turn instead of
     * being turned away, up to {@code network-settings.maxQueuedLogins} waiting logins. Logins are
     * let in the order they arrived. A public address that already holds its share of slots, see
     * {@link #tryAdmit(InetAddress)}, is refused rather than queued.
     *
     * @param address the address the login comes from, or {@code null} when unknown
     * @param resume  run once a queued login has been given its slot; never run for
     *                {@link Admission#ADMITTED} or {@link Admission#REFUSED}. It runs on whatever
     *                thread freed the slot, so it should only hand the login back to its own thread
     * @return whether the login holds a slot now, waits for one, or was refused
     */
    public Admission admit(@Nullable InetAddress address, Runnable resume) {
        synchronized (this.waiting) {
            if (this.waiting.isEmpty() && this.tryAdmit(address)) {
                return Admission.ADMITTED;
            }
            final InetAddress limited = this.limitedAddress(address);
            if (limited != null && this.pendingLoginsByAddress.getOrDefault(limited, 0) >= this.maxPendingLoginsPerAddress) {
                return Admission.REFUSED;
            }
            if (this.waiting.size() >= this.maxWaitingLogins) {
                return Admission.REFUSED;
            }
            this.waiting.add(new Waiter(address, resume));
        }
        this.drainWaiting();
        return Admission.QUEUED;
    }

    /**
     * Drops a login queued by {@link #admit(InetAddress, Runnable)}, as when its session closes
     * before its turn. Does nothing when it is not queued.
     *
     * @param resume the same instance that was passed to {@code admit}
     */
    public void cancelWaiting(Runnable resume) {
        synchronized (this.waiting) {
            this.waiting.removeIf(waiter -> waiter.resume() == resume);
        }
    }

    public int getWaitingLogins() {
        synchronized (this.waiting) {
            return this.waiting.size();
        }
    }

    /**
     * Frees a slot reserved by {@link #tryAdmit(InetAddress)} or {@link #admit(InetAddress, Runnable)},
     * and hands it to the next queued login. Must be called exactly once per successful admission,
     * with the same address.
     */
    public void release(@Nullable InetAddress address) {
        this.pendingLogins.decrementAndGet();
        final InetAddress limited = this.limitedAddress(address);
        if (limited != null) {
            this.releaseAddress(limited);
        }
        this.drainWaiting();
    }

    private void drainWaiting() {
        List<Waiter> admitted = null;
        synchronized (this.waiting) {
            final Iterator<Waiter> iterator = this.waiting.iterator();
            while (iterator.hasNext() && this.pendingLogins.get() < this.maxPendingLogins) {
                final Waiter waiter = iterator.next();
                if (this.tryAdmit(waiter.address())) {
                    iterator.remove();
                    if (admitted == null) {
                        admitted = new ArrayList<>();
                    }
                    admitted.add(waiter);
                }
            }
        }
        if (admitted != null) {
            for (Waiter waiter : admitted) {
                waiter.resume().run();
            }
        }
    }

    private void releaseAddress(InetAddress address) {
        this.pendingLoginsByAddress.computeIfPresent(address, (key, count) -> count > 1 ? count - 1 : null);
    }

    private @Nullable InetAddress limitedAddress(@Nullable InetAddress address) {
        if (address == null || this.maxPendingLoginsPerAddress <= 0
            || address.isLoopbackAddress() || address.isSiteLocalAddress()
            || address.isLinkLocalAddress() || address.isAnyLocalAddress()) {
            return null;
        }
        return address;
    }

    /**
     * Takes a player slot for a login that the server has accepted but whose player is not online
     * yet. Slots taken this way count towards {@code max-players} together with the players that
     * are online.
     *
     * @param onlinePlayers the number of players online right now
     * @param maxPlayers    the player limit
     * @return {@code false} when the server is full
     */
    public boolean tryReservePlayerSlot(int onlinePlayers, int maxPlayers) {
        while (true) {
            int reserved = this.reservedPlayerSlots.get();
            if (onlinePlayers + reserved >= maxPlayers) {
                return false;
            }
            if (this.reservedPlayerSlots.compareAndSet(reserved, reserved + 1)) {
                return true;
            }
        }
    }

    /**
     * Frees a slot taken by {@link #tryReservePlayerSlot(int, int)}, once its player is online or
     * its login has ended. Must be called exactly once per successful reservation.
     */
    public void releasePlayerSlot() {
        this.reservedPlayerSlots.decrementAndGet();
    }

    public int getReservedPlayerSlots() {
        return this.reservedPlayerSlots.get();
    }

    public int getPendingLogins() {
        return this.pendingLogins.get();
    }

    public int getMaxPendingLogins() {
        return this.maxPendingLogins;
    }

    /**
     * Runs a login validation step.
     *
     * @return {@code false} when the step was refused because the validation queue is full or shut down
     */
    public boolean executeValidation(Runnable task) {
        try {
            this.validationExecutor.execute(task);
            return true;
        } catch (RejectedExecutionException e) {
            return false;
        }
    }

    /**
     * Runs a player data read or write. Tasks run one at a time, in submission order.
     *
     * @throws RejectedExecutionException once the service has been shut down
     */
    public void executePlayerData(Runnable task) {
        this.playerDataExecutor.execute(task);
    }

    /**
     * Stops validation and waits for every queued player data write to finish.
     */
    public void shutdown() {
        this.validationExecutor.shutdownNow();
        this.playerDataExecutor.shutdown();
        try {
            if (!this.playerDataExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Player data writes did not finish within {} seconds", SHUTDOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
