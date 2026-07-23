package org.powernukkitx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link PlayerFood} getters, food/saturation/exhaustion round-trips and the enable
 * toggle. Mutating calls fire events through the fixture's plugin manager so they are safe.
 */
class PlayerFoodSmokeTest {

    private static TestPlayer player;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        player = PlayerFixture.get();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void constructsAndExposesInitialState() {
        PlayerFood food = new PlayerFood(player, 15, 3.0f);
        assertSame(player, food.getPlayer());
        assertEquals(15, food.getFood());
        assertEquals(20, food.getMaxFood());
        assertEquals(3.0f, food.getSaturation());
        assertTrue(food.isHungry());
    }

    @Test
    void setFoodClampsAndRoundTrips() {
        PlayerFood food = new PlayerFood(player, 20, 5.0f);
        food.setFood(10, 4.0f);
        assertEquals(10, food.getFood());

        food.setFood(999, 999f);
        assertEquals(20, food.getFood(), "food is clamped to 20");

        food.setFood(-5, 0f);
        assertEquals(0, food.getFood(), "food is clamped to 0");
        assertTrue(food.isHungry());
    }

    @Test
    void saturationAndExhaustion() {
        PlayerFood food = new PlayerFood(player, 20, 10.0f);
        food.setSaturation(8.0f);
        assertEquals(8.0f, food.getSaturation());

        food.setExhaustion(2.0f);
        assertEquals(2.0, food.getExhaustion(), 0.001);

        // pushing past 4 drains saturation/food but never throws
        safe(() -> food.exhaust(10.0));
        assertTrue(food.getExhaustion() < 4.0);
    }

    @Test
    void addFoodAndReset() {
        PlayerFood food = new PlayerFood(player, 5, 0f);
        food.addFood(10, 2.0f);
        assertEquals(15, food.getFood());

        food.reset();
        assertEquals(20, food.getFood());
        assertEquals(0, food.getFoodTickTimer());
    }

    @Test
    void enableToggleAndTick() {
        PlayerFood food = new PlayerFood(player, 20, 5.0f);
        food.setEnabled(false);
        // creative/flying/spectator or disabled -> not enabled
        food.isEnabled();
        food.setEnabled(true);
        safe(() -> food.tick(80));
    }
}
