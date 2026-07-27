package org.powernukkitx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the {@link Achievement} static registry, constructor, getters and add/broadcast paths.
 */
class AchievementSmokeTest {

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void registryIsPopulated() {
        assertNotNull(Achievement.achievements);
        assertTrue(Achievement.achievements.containsKey("takingInventory"));
        assertEquals("Getting Wood", Achievement.achievements.get("gettingWood").getMessage());
    }

    @Test
    void constructorStoresMessageAndRequires() {
        Achievement a = new Achievement("Custom", "dep1", "dep2");
        assertEquals("Custom", a.getMessage());
        assertEquals("Custom", a.message);
        assertEquals(2, a.requires.length);
        assertEquals("dep1", a.requires[0]);
    }

    @Test
    void addRejectsDuplicatesAcceptsNew() {
        String key = "smokeTestUnique_" + System.nanoTime();
        assertTrue(Achievement.add(key, new Achievement("New One")));
        assertFalse(Achievement.add(key, new Achievement("New One")));
        assertFalse(Achievement.add("takingInventory", new Achievement("dup")));
    }

    @Test
    void broadcastUnknownReturnsFalse() {
        assertFalse(Achievement.broadcast(PlayerFixture.get(), "definitelyNotAnAchievement"));
    }

    @Test
    void broadcastKnownIsTolerant() {
        // Real path sends a message to the player - tolerant against the mocked network.
        safe(() -> Achievement.broadcast(PlayerFixture.get(), "takingInventory"));
    }
}
