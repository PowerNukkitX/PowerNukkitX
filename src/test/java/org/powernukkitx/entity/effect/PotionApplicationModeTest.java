package org.powernukkitx.entity.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PotionApplicationModeTest {

    @Test
    void valueOfRoundTrip() {
        for (PotionApplicationMode mode : PotionApplicationMode.values()) {
            assertSame(mode, PotionApplicationMode.valueOf(mode.name()));
        }
    }

    @Test
    void drinkKeepsFullDuration() {
        assertEquals(1200, PotionApplicationMode.DRINK.scaleDuration(1200));
        assertEquals(0, PotionApplicationMode.DRINK.scaleDuration(0));
    }

    @Test
    void splashKeepsFullDuration() {
        assertEquals(1200, PotionApplicationMode.SPLASH.scaleDuration(1200));
    }

    @Test
    void lingeringIsQuarter() {
        assertEquals(300, PotionApplicationMode.LINGERING.scaleDuration(1200));
    }

    @Test
    void arrowIsEighth() {
        assertEquals(150, PotionApplicationMode.ARROW.scaleDuration(1200));
    }

    @Test
    void scaleTruncatesToInt() {
        // 10 * 0.125 = 1.25 -> 1
        assertEquals(1, PotionApplicationMode.ARROW.scaleDuration(10));
    }
}
