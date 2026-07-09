package cn.nukkit.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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
