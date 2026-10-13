package org.powernukkitx.network.process;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.jetbrains.annotations.ApiStatus;
import org.powernukkitx.config.category.NetworkSettings;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
 */
@Slf4j
@ApiStatus.Internal
public final class LoginService {
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30L;

    private final ThreadPoolExecutor validationExecutor;
    private final ExecutorService playerDataExecutor;
    private final AtomicInteger pendingLogins = new AtomicInteger();
    private final int maxPendingLogins;

    public LoginService(NetworkSettings settings) {
        final int processors = Runtime.getRuntime().availableProcessors();
        final int threads = settings.loginThreads() > 0 ? settings.loginThreads() : Math.max(2, processors / 2);
        this.maxPendingLogins = settings.maxPendingLogins() > 0 ? settings.maxPendingLogins() : Math.max(16, processors * 4);

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
     *
     * @return {@code false} when {@link #getMaxPendingLogins()} logins are already in progress
     */
    public boolean tryAdmit() {
        while (true) {
            int current = this.pendingLogins.get();
            if (current >= this.maxPendingLogins) {
                return false;
            }
            if (this.pendingLogins.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    /**
     * Frees a slot reserved by {@link #tryAdmit()}. Must be called exactly once per successful admission.
     */
    public void release() {
        this.pendingLogins.decrementAndGet();
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
     * Waits until every player data task submitted so far has run.
     *
     * @param timeoutMillis how long to wait at most
     * @return {@code false} when the wait timed out or was interrupted
     */
    public boolean awaitPlayerData(long timeoutMillis) {
        try {
            this.playerDataExecutor.submit(() -> {
            }).get(timeoutMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (RejectedExecutionException | ExecutionException e) {
            return true;
        } catch (TimeoutException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
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
