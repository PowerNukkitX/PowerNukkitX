package org.powernukkitx.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkitx.GameMockExtension;
import org.powernukkitx.Server;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(GameMockExtension.class)
class EntityLeashTest {

    @Test
    void missingHolderLookupIsRetriedOnlyOncePerSecond(Level level, Server server) {
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        assertNotNull(sheep);
        UUID missingHolder = UUID.randomUUID();
        long previousTickDuration = server.getNanosPerTick();

        try {
            doReturn(50_000_000L).when(server).getNanosPerTick();
            sheep.leashHolderUuid = missingHolder;

            for (int tick = 0; tick < 20; tick++) {
                sheep.updateLeash(1);
            }
            verify(server, times(1)).getPlayer(missingHolder);
            assertTrue(sheep.isLeashed());

            sheep.updateLeash(1);
            verify(server, times(2)).getPlayer(missingHolder);
        } finally {
            sheep.close();
            doReturn(previousTickDuration).when(server).getNanosPerTick();
        }
    }

    @Test
    void missingHolderReleasesSheepAfterFiveSeconds(Level level, Server server, TestPlayer player) {
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        assertNotNull(sheep);
        long previousTickDuration = server.getNanosPerTick();

        try {
            doReturn(100_000_000L).when(server).getNanosPerTick();
            sheep.leashHolderUuid = UUID.randomUUID();

            for (int tick = 0; tick < 49; tick++) {
                sheep.updateLeash(1);
            }
            assertTrue(sheep.isLeashed());

            sheep.updateLeash(1);
            assertFalse(sheep.isLeashed());
            assertTrue(sheep.useLeadOn(player));
            assertSame(player, sheep.getLeashedTo());
        } finally {
            sheep.close();
            doReturn(previousTickDuration).when(server).getNanosPerTick();
        }
    }

    @Test
    void sneakingTransfersPlayerLeashedSheepToHappyGhastWithoutConsumingLead(Level level, TestPlayer player) {
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        Entity happyGhast = Entity.createEntity(EntityID.HAPPY_GHAST, new Position(2, 80, 0, level));
        assertNotNull(sheep);
        assertNotNull(happyGhast);

        try {
            sheep.setLeashedTo(player);
            player.setSneaking(true);

            assertFalse(happyGhast.onInteract(player, Item.get(Item.LEAD), new Vector3(2, 80, 0)));
            assertSame(happyGhast, sheep.getLeashedTo());

            sheep.saveNBT();
            assertEquals(happyGhast.getUniqueId().toString(),
                    sheep.getNbt().getCompound("Leash").getString("UUID"));
        } finally {
            player.setSneaking(false);
            sheep.close();
            happyGhast.close();
        }
    }

    @Test
    void sneakingWithoutLeashedEntitiesConsumesLeadForNewAttachment(Level level, TestPlayer player) {
        Entity happyGhast = Entity.createEntity(EntityID.HAPPY_GHAST, new Position(2, 80, 0, level));
        assertNotNull(happyGhast);

        try {
            player.setSneaking(true);

            assertTrue(happyGhast.onInteract(player, Item.get(Item.LEAD), new Vector3(2, 80, 0)));
            assertSame(player, happyGhast.getLeashedTo());
        } finally {
            player.setSneaking(false);
            happyGhast.close();
        }
    }

    @Test
    void takingSheepFromKnotDoesNotConsumeLead(Level level, TestPlayer player) {
        Vector3 fencePos = new Vector3(2, 80, 0);
        Block previousBlock = level.getBlock(fencePos);
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        assertNotNull(sheep);
        EntityLeashKnot knot = null;

        try {
            level.setBlock(fencePos, Block.get(BlockID.OAK_FENCE));
            knot = EntityLeashKnot.getOrCreate(level.getBlock(fencePos));
            assertNotNull(knot);
            sheep.setLeashedTo(knot);

            assertFalse(sheep.onInteract(player, Item.get(Item.LEAD), new Vector3()));
            assertSame(player, sheep.getLeashedTo());
        } finally {
            sheep.close();
            if (knot != null) knot.close();
            level.setBlock(fencePos, previousBlock);
        }
    }

    @Test
    void missingKnotIsRecreatedWhenFenceStillExists(Level level) {
        Vector3 fencePos = new Vector3(2, 80, 0);
        Block previousBlock = level.getBlock(fencePos);
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        assertNotNull(sheep);
        EntityLeashKnot knot = null;

        try {
            level.setBlock(fencePos, Block.get(BlockID.OAK_FENCE));
            knot = EntityLeashKnot.getOrCreate(level.getBlock(fencePos));
            assertNotNull(knot);
            sheep.setLeashedTo(knot);
            knot.close();

            sheep.updateLeash(1);

            EntityLeashKnot recreated = EntityLeashKnot.getKnotAt(level, 2, 80, 0);
            assertNotNull(recreated);
            knot = recreated;
            assertFalse(recreated.closed);
            assertSame(recreated, sheep.getLeashedTo());
            assertTrue(sheep.isLeashed());
        } finally {
            sheep.close();
            if (knot != null) knot.close();
            level.setBlock(fencePos, previousBlock);
        }
    }
}
