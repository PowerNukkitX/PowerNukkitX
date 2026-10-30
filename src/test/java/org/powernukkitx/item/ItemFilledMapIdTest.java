package org.powernukkitx.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for filled-map identifiers.
 *
 * Map IDs are persisted with the item and used by the client to associate map images.
 * They must therefore be positive and newly created maps should receive distinct IDs.
 */
class ItemFilledMapIdTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @Test
    void newMapsGetPositiveDistinctIds() {
        final int mapCount = 1_000;
        final Set<Long> ids = new HashSet<>();

        for (int i = 0; i < mapCount; i++) {
            final long id = new ItemFilledMap().getMapId();

            assertTrue(id > 0, "map id must be positive, got " + id);
            ids.add(id);
        }

        assertEquals(mapCount, ids.size(), "each newly created map should have a distinct ID");
    }
}

