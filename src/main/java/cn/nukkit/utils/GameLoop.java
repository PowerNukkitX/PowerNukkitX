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
    private final float[] tpsSummary = new float[20];
    private final float[] msptSummary = new float[20];
    @Getter
    private long tick;

    private long lastSecondTime = System.currentTimeMillis();
    private int tickCounter = 0;

    private long lastOverloadWarnTime = 0;
    private int overloadTickCount = 0;
    private static final int OVERLOAD_TICK_THRESHOLD = 10;
    private static final long OVERLOAD_WARN_INTERVAL_MS = 1000; // 1 sec

    private GameLoop(Runnable onStart, Consumer<GameLoop> onTick, Runnable onStop, int loopCountPerSec, long currentTick) {
        if (loopCountPerSec <= 0) {
            throw new IllegalArgumentException("Loop count per second must be greater than 0! (loopCountPerSec=" + loopCountPerSec + ")");
        }
        this.onStart = onStart;
        this.onTick = onTick;
        this.onStop = onStop;
        this.loopCountPerSec = loopCountPerSec;
        this.tick = currentTick;
        Arrays.fill(tpsSummary, loopCountPerSec);
        Arrays.fill(msptSummary, 0f);
    }

    public static GameLoopBuilder builder() {
        return new GameLoopBuilder();
    }

    public float getTickUsage() {
        return getMSPT() / (1000f / loopCountPerSec);
    }

    public long getNextTick() {
        long tickIntervalMillis = 1000L / loopCountPerSec;
        return lastSecondTime + tickCounter * tickIntervalMillis;
    }

    public float getTPS() {
        float sum = 0;
        for (float tps : tpsSummary) {
            sum += tps;
        }
        return sum / tpsSummary.length;
    }

    public float getMSPT() {
        float sum = 0;
        for (float mspt : msptSummary) {
            sum += mspt;
        }
        return sum / msptSummary.length;
    }

    public void startLoop() {
        onStart.run();
        long idealNanoPerTick = 1_000_000_000L / loopCountPerSec;

        while (isRunning.get()) {
            long startTickTime = System.nanoTime();
            onTick.accept(this);
            tick++;
            tickCounter++;

            long timeTaken = System.nanoTime() - startTickTime;
            updateMSPT(timeTaken);

            long now = System.currentTimeMillis();
            if (now - lastSecondTime >= 1000) {
                updateTPS(tickCounter);
                tickCounter = 0;
                lastSecondTime = now;
            }

            long sleepTime = idealNanoPerTick - (System.nanoTime() - startTickTime);
            if (sleepTime > 0) {
                try {
                    TimeUnit.NANOSECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.error("GameLoop interrupted during sleep", e);
                    onStop.run();
                    return;
                }
            } else {
                log.debug("GameLoop is running behind! Time taken: {} ns, ideal: {} ns", timeTaken, idealNanoPerTick);
                Thread.yield();
            }
        }
    }

    private void updateTPS(int ticksLastSecond) {
        System.arraycopy(tpsSummary, 1, tpsSummary, 0, tpsSummary.length - 1);
        tpsSummary[tpsSummary.length - 1] = ticksLastSecond;

        if (ticksLastSecond < loopCountPerSec) {
            overloadTickCount++;
        } else {
            overloadTickCount = 0;
        }

        long now = System.currentTimeMillis();
        if (overloadTickCount >= OVERLOAD_TICK_THRESHOLD && now - lastOverloadWarnTime >= OVERLOAD_WARN_INTERVAL_MS) {
            float tps = getTPS();
            float mspt = getMSPT();
            log.warn("Can't keep up! TPS: {}, MSPT: {}", String.format("%.2f", tps), String.format("%.2f", mspt));
            lastOverloadWarnTime = now;
            overloadTickCount = 0;
        }
    }

    private void updateMSPT(long timeTaken) {
        float mspt = timeTaken / 1_000_000f; // nanos to millis
        float smoothingFactor = 0.9f;
        msptSummary[0] = msptSummary[0] * smoothingFactor + mspt * (1 - smoothingFactor);
        System.arraycopy(msptSummary, 1, msptSummary, 0, msptSummary.length - 1);
        msptSummary[msptSummary.length - 1] = mspt;
    }

    public void stop() {
        isRunning.set(false);
        onStop.run();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public static class GameLoopBuilder {
        private Runnable onStart = () -> {};
        private Consumer<GameLoop> onTick = gameLoop -> {};
        private Runnable onStop = () -> {};
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
