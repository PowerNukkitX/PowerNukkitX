package org.powernukkitx.entity;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUsePredictedResult;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseInventoryTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityCollisionToggleTest {

    private static Level level;
    private Entity entity;

    @BeforeAll
    static void setup() {
        level = PlayerFixture.get().getLevel();
    }

    @AfterEach
    void closeEntity() {
        if (entity != null) {
            entity.close();
        }
    }

    private Entity spawnZombie(Vector3 at) {
        entity = Entity.createEntity(EntityID.ZOMBIE, new Position(at.x + 0.5, at.y, at.z + 0.5, level));
        assertNotNull(entity);
        return entity;
    }

    private static ItemUseInventoryTransaction clickTop() {
        ItemUseInventoryTransaction data = new ItemUseInventoryTransaction();
        data.setClickPosition(Vector3f.from(0.5f, 1f, 0.5f));
        data.setClientInteractPrediction(ItemUsePredictedResult.SUCCESS);
        return data;
    }

    @Test
    void toggleDrivesCanCollideAndClientFlag() {
        Entity zombie = spawnZombie(new Vector3(0, 80, 0));
        assertTrue(zombie.isCollisionEnabled());
        assertTrue(zombie.canCollide());

        zombie.setCollisionEnabled(false);
        assertFalse(zombie.canCollide());
        assertFalse(zombie.getDataFlag(ActorFlags.HAS_COLLISION));

        zombie.setCollisionEnabled(true);
        assertTrue(zombie.canCollide());
        assertTrue(zombie.getDataFlag(ActorFlags.HAS_COLLISION));
    }

    @Test
    void disabledEntityDoesNotObstructPlacement() {
        Vector3 below = new Vector3(40, 5, 40);
        Vector3 target = below.up();
        level.setBlock(below, Block.get(BlockID.STONE));
        level.setBlock(target, Block.get(BlockID.AIR));
        Entity zombie = spawnZombie(target);

        level.useItemOn(below, Item.get(BlockID.STONE), BlockFace.UP, clickTop());
        assertEquals(BlockID.AIR, level.getBlock(target).getId(), "a collidable entity must block placement");

        zombie.setCollisionEnabled(false);
        level.useItemOn(below, Item.get(BlockID.STONE), BlockFace.UP, clickTop());
        assertEquals(BlockID.STONE, level.getBlock(target).getId());
    }
}
