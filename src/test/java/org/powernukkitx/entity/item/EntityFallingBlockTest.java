package org.powernukkitx.entity.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityFallingBlockTest {

    private static Level level;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void missingBlockTagClosesInsteadOfFailingConstruction() {
        IChunk chunk = level.getChunk(0, 0, true);
        Entity entity = Entity.createEntity(Entity.FALLING_BLOCK, chunk, Entity.getDefaultNBT(new Vector3(0.5, 100, 0.5)));

        assertInstanceOf(EntityFallingBlock.class, entity);
        assertTrue(entity.isClosed());
        assertFalse(chunk.getEntities().containsKey(entity.runtimeId()));
        assertNull(level.getEntity(entity.runtimeId()));
    }
}
