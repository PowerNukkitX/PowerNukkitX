package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that {@link GameLoop} paces correctly at tick rates above 1000 TPS,
 * where millisecond-based sleeping cannot work.
 */
class GameLoopPacingTest {

    private static int runForOneSecond(int targetTps) throws InterruptedException {
        AtomicInteger ticks = new AtomicInteger();
        GameLoop loop = GameLoop.builder()
                .onTick(l -> ticks.incrementAndGet())
                .loopCountPerSec(targetTps)
                .build();

        Thread thread = new Thread(loop::startLoop, "GameLoopPacingTest-" + targetTps);
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(1000);
        loop.stop();
        thread.interrupt();
        thread.join(2000);
        return ticks.get();
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void pacesAtMaximumConfigurableRate() throws Exception {
        // 100,000 TPS = 10 µs per tick — the highest rate the server clamp allows
        int targetTps = 100_000;
        int count = runForOneSecond(targetTps);

        // Never meaningfully faster than configured (pacing must throttle an empty tick body)
        assertTrue(count <= targetTps + targetTps / 10,
                "loop ran too fast: " + count + " ticks in 1s (target " + targetTps + ")");
        // Should achieve a large fraction of the target on any reasonable machine;
        // generous lower bound to tolerate CI noise
        assertTrue(count >= targetTps / 4,
                "loop ran too slow: " + count + " ticks in 1s (target " + targetTps + ")");
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void ticksNeverOverlap() throws Exception {
        // Serialization guarantee: onTick must never run concurrently, even when the
        // external tick() entry point is hammered from other threads while the loop's
        // own thread is running startLoop(). Guarded by GameLoop.tickLock.
        AtomicInteger concurrent = new AtomicInteger();
        AtomicInteger maxConcurrent = new AtomicInteger();
        GameLoop loop = GameLoop.builder()
                .onTick(l -> {
                    int now = concurrent.incrementAndGet();
                    maxConcurrent.accumulateAndGet(now, Math::max);
                    long end = System.nanoTime() + 100_000; // 100 µs body to widen any race window
                    while (System.nanoTime() < end) Thread.onSpinWait();
                    concurrent.decrementAndGet();
                })
                .loopCountPerSec(100_000)
                .build();

        Thread loopThread = new Thread(loop::startLoop, "GameLoopOverlapTest-loop");
        loopThread.setDaemon(true);
        loopThread.start();

        int hammers = 4;
        ExecutorService pool = Executors.newFixedThreadPool(hammers);
        long deadline = System.currentTimeMillis() + 1000;
        for (int i = 0; i < hammers; i++) {
            pool.submit(() -> {
                while (System.currentTimeMillis() < deadline) {
                    loop.tick();
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        loop.stop();
        loopThread.interrupt();
        loopThread.join(2000);

        assertEquals(1, maxConcurrent.get(),
                "onTick executed concurrently (max " + maxConcurrent.get() + " threads inside at once)");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void rateChangeableWhileRunning() throws Exception {
        AtomicInteger ticks = new AtomicInteger();
        GameLoop loop = GameLoop.builder()
                .onTick(l -> ticks.incrementAndGet())
                .loopCountPerSec(20)
                .build();

        Thread thread = new Thread(loop::startLoop, "GameLoopRateChangeTest");
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(200);
        int slowTicks = ticks.get();
        assertTrue(slowTicks <= 20, "expected ≤20 ticks at 20 TPS in 200ms, got " + slowTicks);

        loop.setLoopCountPerSec(10_000);
        assertEquals(100_000L, loop.getNanosPerTick());
        Thread.sleep(500);
        loop.stop();
        thread.interrupt();
        thread.join(2000);

        int fastTicks = ticks.get() - slowTicks;
        assertTrue(fastTicks >= 500,
                "rate change did not take effect: only " + fastTicks + " ticks in 500ms at 10k TPS");
    }
}
