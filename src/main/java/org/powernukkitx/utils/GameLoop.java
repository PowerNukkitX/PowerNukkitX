package org.powernukkitx.utils;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * Game loop class
 *
 * @author daoge_cmd (Allay Project)
 * @since 2023/4/14
 */
@Slf4j
public final class GameLoop {
    /**
     * Margin left for the spin phase of {@link #waitUntilNanos}. OS sleep primitives
     * (parkNanos) can oversleep by roughly this much, so the coarse phase stops early
     * and the remainder is spun precisely.
     */
    public static final long SPIN_MARGIN_NANOS = 2_000_000L;
    /**
     * Tick periods at or above this duration are paced with plain parkNanos (no spin
     * phase) — sub-millisecond jitter is irrelevant there and spinning would waste CPU.
     */
    public static final long SPIN_ACTIVATION_NANOS = 10_000_000L;
    /**
     * If the loop falls further behind than this, missed ticks are dropped instead of
     * being executed back-to-back to catch up (prevents a death spiral after GC pauses
     * or long stalls).
     */
    public static final long CATCHUP_RESET_NANOS = 1_000_000_000L;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Object tickLock = new Object();
    private final Runnable onStart;
    private final Consumer<GameLoop> onTick;
    private final Runnable onStop;

    @Getter
    private volatile int loopCountPerSec;
    private volatile long nanosPerTick;
    private volatile Thread loopThread;
    private final float[] tickSummary = new float[20];
    private final float[] MSPTSummary = new float[20];
    /**
     * Circular write index into the summary arrays. Overwriting one slot per tick is
     * far cheaper than the previous arraycopy shift, which costs real budget at
     * sub-microsecond tick periods.
     */
    private int summaryIndex;
    /**
     * Long, not int: an int tick counter wraps after ~6 hours at 100k TPS
     * (~36 minutes at 1M TPS), breaking absolute tick-deadline comparisons.
     */
    @Getter
    private long tick;

    private GameLoop(Runnable onStart, Consumer<GameLoop> onTick, Runnable onStop, int loopCountPerSec) {
        if (loopCountPerSec <= 0)
            throw new IllegalArgumentException("Loop count per second must be greater than 0! (loopCountPerSec=" + loopCountPerSec + ")");
        this.onStart = onStart;
        this.onTick = onTick;
        this.onStop = onStop;
        this.loopCountPerSec = loopCountPerSec;
        this.nanosPerTick = 1_000_000_000L / loopCountPerSec;
        Arrays.fill(tickSummary, loopCountPerSec);
        Arrays.fill(MSPTSummary, 0f);
    }

    public static GameLoopBuilder builder() {
        return new GameLoopBuilder();
    }

    /**
     * Changes the tick rate of a running loop. Takes effect on the next pacing decision.
     */
    public void setLoopCountPerSec(int loopCountPerSec) {
        Preconditions.checkArgument(loopCountPerSec > 0 && loopCountPerSec <= 1_000_000_000);
        this.loopCountPerSec = loopCountPerSec;
        this.nanosPerTick = 1_000_000_000L / loopCountPerSec;
    }

    public long getNanosPerTick() {
        return nanosPerTick;
    }

    public float getTickUsage() {
        return getMSPT() / (nanosPerTick / 1_000_000f);
    }

    public float getTps() {
        float sum = 0;
        int count = tickSummary.length;
        for (float tick : tickSummary) {
            sum += tick;
        }
        return sum / count;
    }

    public float getMSPT() {
        float sum = 0;
        int count = MSPTSummary.length;
        for (float mspt : MSPTSummary) {
            sum += mspt;
        }
        return sum / count;
    }

    public void tick() {
        synchronized (tickLock) {
            if (!isRunning.get()) return;
            long startTickTime = System.nanoTime();
            onTick.accept(this);
            tick++;
            long timeTakenToTick = System.nanoTime() - startTickTime;
            updateMSTP(timeTakenToTick, MSPTSummary);
            updateTPS(timeTakenToTick);
        }
    }

    /**
     * Runs {@link #startLoop()} on a new dedicated daemon thread owned by this GameLoop.
     * A dedicated thread (rather than a scheduled executor) is required for tick periods
     * below the OS timer granularity (~1 ms): the pacer must own the thread to combine
     * parkNanos with a spin phase. One loop = one thread; calling twice is an error.
     */
    public synchronized Thread startThread(String name) {
        Preconditions.checkState(loopThread == null, "GameLoop thread already started");
        Thread thread = new Thread(() -> {
            try {
                startLoop();
            } catch (Throwable t) {
                log.error("GameLoop thread {} died", name, t);
            }
        }, name);
        thread.setDaemon(true);
        this.loopThread = thread;
        thread.start();
        return thread;
    }

    /**
     * Whether the thread created by {@link #startThread} is still running.
     */
    public boolean isThreadAlive() {
        Thread thread = this.loopThread;
        return thread != null && thread.isAlive();
    }

    /**
     * Interrupts the thread created by {@link #startThread}, waking it from its
     * inter-tick wait so a stopped loop exits promptly. No-op if none was started.
     */
    public void interruptThread() {
        Thread thread = this.loopThread;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void startLoop() {
        isRunning.set(true);
        onStart.run();
        long nextTickNanos = System.nanoTime();
        while (isRunning.get()) {
            long startTickTime = System.nanoTime();
            synchronized (tickLock) {
                if (!isRunning.get()) break;
                onTick.accept(this);
                tick++;
            }
            long timeTakenToTick = System.nanoTime() - startTickTime;
            updateMSTP(timeTakenToTick, MSPTSummary);
            updateTPS(timeTakenToTick);

            nextTickNanos += nanosPerTick;
            long now = System.nanoTime();
            if (now - nextTickNanos > CATCHUP_RESET_NANOS) {
                nextTickNanos = now;
            } else {
                waitUntilNanos(nextTickNanos, nanosPerTick < SPIN_ACTIVATION_NANOS ? SPIN_MARGIN_NANOS : 0L);
            }
            if (Thread.currentThread().isInterrupted()) {
                log.debug("GameLoop thread {} interrupted, stopping", Thread.currentThread().getName());
                break;
            }
        }
        onStop.run();
    }

    /**
     * Waits until {@code System.nanoTime()} reaches {@code deadlineNanos}.
     * <p>
     * Coarse phase parks the thread until {@code spinMarginNanos} before the deadline;
     * the remainder is busy-spun with {@link Thread#onSpinWait()} for sub-millisecond
     * precision. Pass {@code spinMarginNanos = 0} to skip spinning entirely (plain park,
     * millisecond-class precision, no CPU burn). Returns early if the thread is interrupted.
     */
    public static void waitUntilNanos(long deadlineNanos, long spinMarginNanos) {
        long remaining;
        while ((remaining = deadlineNanos - System.nanoTime()) > spinMarginNanos) {
            LockSupport.parkNanos(remaining - spinMarginNanos);
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        if (spinMarginNanos > 0) {
            while (System.nanoTime() - deadlineNanos < 0) {
                Thread.onSpinWait();
            }
        }
    }

    private void updateTPS(long timeTakenToTick) {
        float tick = Math.max(0, Math.min(loopCountPerSec, 1000000000f / (timeTakenToTick == 0 ? 1 : timeTakenToTick)));
        tickSummary[summaryIndex] = tick;
        summaryIndex = (summaryIndex + 1) % tickSummary.length;
    }

    private void updateMSTP(float timeTakenToTick, float[] mstpSummary) {
        mstpSummary[summaryIndex] = timeTakenToTick / 1000000f;
    }

    public void stop() {
        isRunning.set(false);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public void setRunning(boolean value) {
        isRunning.set(value);
    }

    public Object getTickLock() {
        return tickLock;
    }

    public static class GameLoopBuilder {
        private Runnable onStart = () -> {};
        private Consumer<GameLoop> onTick = gameLoop -> {};
        private Runnable onStop = () -> {};
        private int loopCountPerSec = 20;

        public GameLoopBuilder onStart(Runnable onStart) {
            this.onStart = onStart;
            return this;
        }

        public GameLoopBuilder onTick(Consumer<GameLoop> onTick) {
            this.onTick = onTick;
            return this;
        }

        public GameLoopBuilder onStop(Runnable onStop) {
            this.onStop = onStop;
            return this;
        }

        public GameLoopBuilder loopCountPerSec(int loopCountPerSec) {
            Preconditions.checkArgument(loopCountPerSec > 0 && loopCountPerSec <= 1_000_000_000);
            this.loopCountPerSec = loopCountPerSec;
            return this;
        }

        public GameLoop build() {
            return new GameLoop(onStart, onTick, onStop, loopCountPerSec);
        }
    }
}
