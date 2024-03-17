package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Allay Project 2023/4/14
 *
 * @author daoge_cmd
 */
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

    private GameLoop(Runnable onStart, Consumer<GameLoop> onTick, Runnable onStop, int loopCountPerSec) {
        if (loopCountPerSec <= 0)
            throw new IllegalArgumentException("Loop count per second must be greater than 0! (loopCountPerSec=" + loopCountPerSec + ")");
        this.onStart = onStart;
        this.onTick = onTick;
        this.onStop = onStop;
        this.loopCountPerSec = loopCountPerSec;
        Arrays.fill(tickSummary, 20f);
        Arrays.fill(MSPTSummary, 0f);
    }

    public static GameLoopBuilder builder() {
        return new GameLoopBuilder();
    }

    public float getTickUsage() {
        return getMSPT() / (1000f / loopCountPerSec);
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

    public void startLoop() {
        onStart.run();
        long nanoSleepTime = 0;
        long idealNanoSleepPerTick = 1000000000 / loopCountPerSec;
        while (isRunning.get()) {
            // Figure out how long it took to tick
            long startTickTime = System.nanoTime();
            onTick.accept(this);
            tick++;
            long timeTakenToTick = System.nanoTime() - startTickTime;
            updateMSTP(timeTakenToTick, MSPTSummary);
            updateTPS(timeTakenToTick);

            long sumOperateTime = System.nanoTime() - startTickTime;
            // Sleep for the ideal time but take into account the time spent running the tick
            nanoSleepTime += idealNanoSleepPerTick - sumOperateTime;
            long sleepStart = System.nanoTime();
            try {
                if (nanoSleepTime > 0) {
                    // noinspection BusyWait
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(nanoSleepTime));
                }
            } catch (InterruptedException exception) {
                log.error("GameLoop interrupted", exception);
                onStop.run();
                return;
            }
            // How long did it actually take to sleep?
            // If we didn't sleep for the correct amount,
            // take that into account for the next sleep by
            // leaving extra/less for the next sleep.
            nanoSleepTime -= System.nanoTime() - sleepStart;
        }
        onStop.run();
    }

    private void updateTPS(long timeTakenToTick) {
        float tick = Math.max(0, Math.min(20, 1000000000f / (timeTakenToTick == 0 ? 1 : timeTakenToTick)));
        System.arraycopy(tickSummary, 1, tickSummary, 0, tickSummary.length - 1);
        tickSummary[tickSummary.length - 1] = tick;
    }

    private void updateMSTP(float timeTakenToTick, float[] mstpSummary) {
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
            Preconditions.checkArgument(loopCountPerSec > 0 && loopCountPerSec <= 1024);
            this.loopCountPerSec = loopCountPerSec;
            return this;
        }

        public GameLoop build() {
            return new GameLoop(onStart, onTick, onStop, loopCountPerSec);
        }
    }
}
