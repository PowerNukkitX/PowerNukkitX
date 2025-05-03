package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public final class GameLoop {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Runnable onStart;
    private final Consumer<GameLoop> onTick;
    private final Runnable onStop;
    @Getter
    private final int loopCountPerSec;
    private final float[] tickSummary = new float[20];
    private final float[] MSPTSummary = new float[20];
    @Getter
    private long tick;

    private GameLoop(Runnable onStart, Consumer<GameLoop> onTick, Runnable onStop, int loopCountPerSec, long currentTick) {
        if (loopCountPerSec <= 0) {
            throw new IllegalArgumentException("Loop count per second must be greater than 0! (loopCountPerSec=" + loopCountPerSec + ")");
        }
        this.onStart = onStart;
        this.onTick = onTick;
        this.onStop = onStop;
        this.loopCountPerSec = loopCountPerSec;
        this.tick = currentTick;
        Arrays.fill(tickSummary, 20f);
        Arrays.fill(MSPTSummary, 0f);
    }

    public static GameLoopBuilder builder() {
        return new GameLoopBuilder();
    }

    public float getTickUsage() {
        return getMSPT() / (1000f / loopCountPerSec);
    }

    public float getTPS() {
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

    public void startLoop() {
        onStart.run();
        long nanoSleepTime = 0;
        long idealNanoSleepPerTick = 1_000_000_000L / loopCountPerSec;
        long lastWarnTime = 0;
        long warnIntervalNanos = 1_000_000_000L; // warn at most once per second

        long overloadWarnTime = 0;
        long overloadWarnThreshold = 10;  // Warn after 10 ticks of overload

        while (isRunning.get()) {
            long startTickTime = System.nanoTime();
            onTick.accept(this);
            tick++;
            long timeTakenToTick = System.nanoTime() - startTickTime;
            updateMSTP(timeTakenToTick, MSPTSummary);
            updateTPS(timeTakenToTick);

            long sumOperateTime = System.nanoTime() - startTickTime;
            nanoSleepTime += idealNanoSleepPerTick - sumOperateTime;

            if (nanoSleepTime <= 0) {
                // Clamp sleep time to zero to avoid negative buildup
                nanoSleepTime = 0;

                // Throttle warning logs to avoid spam
                long now = System.nanoTime();
                if (now - lastWarnTime >= warnIntervalNanos) {
                    log.warn("No sleep time available. Server is overloaded!");
                    lastWarnTime = now;
                }

                // Prevent busy spin — sleep at least 1 ms to yield CPU
                try {
                    // Sleep for a minimal amount of time, e.g., 1ms
                    Thread.sleep(1);  // Sleep in milliseconds to avoid busy spinning
                } catch (InterruptedException exception) {
                    log.error("GameLoop interrupted during fallback sleep", exception);
                    onStop.run();
                    return;
                }
            } else {
                // If sleep time is greater than 0, we sleep for the required amount in nanoseconds.
                long sleepTimeToUse = Math.min(nanoSleepTime, idealNanoSleepPerTick);

                long startTime = System.nanoTime();
                while (System.nanoTime() - startTime < sleepTimeToUse) {
                    // Loop until the time has passed (busy-waiting)
                    // This helps us avoid precision issues with Thread.sleep() for sub-millisecond sleeps
                }
                nanoSleepTime -= sleepTimeToUse;

            }

        }
        onStop.run();
    }

    private void updateTPS(long timeTakenToTick) {
        // Calculate the TPS from the current tick time, but add smoothing (exponential moving average)
        float tickRate = Math.max(0, Math.min(20, 1000000000f / (timeTakenToTick == 0 ? 1 : timeTakenToTick)));

        // Smooth out the TPS using a weighted moving average for better stability
        float smoothingFactor = 0.9f;  // Tweak this value based on your needs for smoothness
        tickSummary[0] = tickSummary[0] * smoothingFactor + tickRate * (1 - smoothingFactor); // Smoothing logic

        // Shift the array
        System.arraycopy(tickSummary, 1, tickSummary, 0, tickSummary.length - 1);
        tickSummary[tickSummary.length - 1] = tickRate;
    }

    private void updateMSTP(long timeTakenToTick, float[] mstpSummary) {
        // Smooth the MSPT values similarly
        float smoothingFactor = 0.9f;
        MSPTSummary[0] = MSPTSummary[0] * smoothingFactor + (timeTakenToTick / 1000000f) * (1 - smoothingFactor); // Smoothing logic

        // Shift the array
        System.arraycopy(mstpSummary, 1, mstpSummary, 0, mstpSummary.length - 1);
        mstpSummary[mstpSummary.length - 1] = timeTakenToTick / 1000000f;
    }

    public void stop() {
        isRunning.set(false);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public static class GameLoopBuilder {
        private Runnable onStart = () -> {
        };
        private Consumer<GameLoop> onTick = gameLoop -> {
        };
        private Runnable onStop = () -> {
        };
        private int loopCountPerSec = 20;
        private long currentTick = 0;

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
            Preconditions.checkArgument(loopCountPerSec > 0 && loopCountPerSec <= 1024);
            this.loopCountPerSec = loopCountPerSec;
            return this;
        }

        public GameLoopBuilder currentTick(long currentTick) {
            this.currentTick = currentTick;
            return this;
        }

        public GameLoop build() {
            return new GameLoop(onStart, onTick, onStop, loopCountPerSec, currentTick);
        }
    }
}
