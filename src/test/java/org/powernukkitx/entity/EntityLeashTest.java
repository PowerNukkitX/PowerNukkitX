package org.powernukkitx.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkitx.GameMockExtension;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GameMockExtension.class)
class EntityLeashTest {

    @Test
    void sneakingTransfersPlayerLeashedSheepToHappyGhast(Level level, TestPlayer player) {
        Entity sheep = Entity.createEntity(EntityID.SHEEP, new Position(0, 80, 0, level));
        Entity happyGhast = Entity.createEntity(EntityID.HAPPY_GHAST, new Position(2, 80, 0, level));
        assertNotNull(sheep);
        assertNotNull(happyGhast);

        try {
            sheep.setLeashedTo(player);
            player.setSneaking(true);

            assertTrue(happyGhast.onInteract(player, Item.get(Item.LEAD), new Vector3(2, 80, 0)));
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
}
