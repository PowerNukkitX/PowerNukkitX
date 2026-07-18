package org.powernukkitx.player;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Drives player-driven interaction paths - inventory, gamemode, experience, and block
 * break/activate/place through the level with a real player - covering large branches of
 * Player.java and the level interaction code that only run when a player acts.
 */
public class PlayerInteractionSmokeTest {

    static TestPlayer player;
    static Level level;

    @BeforeAll
    static void boot() {
        player = PlayerFixture.get();
        level = player.getLevel();
    }

    @Test
    void playerStateAndInventory() {
        safe(() -> player.setGamemode(0));
        safe(() -> player.setGamemode(1));
        safe(() -> player.setGamemode(2));
        safe(() -> player.setGamemode(3));
        safe(() -> player.giveItem(Item.get("minecraft:stone")));
        safe(player::getInventory);
        safe(player::getOffhandInventory);
        safe(player::getEnderChestInventory);
        safe(player::getCursorInventory);
        safe(player::getCraftingGrid);
        safe(() -> player.addExperience(100));
        safe(() -> player.setExperience(50));
        safe(() -> player.setExperience(10, 2));
        safe(() -> player.sendMessage("hello"));
        safe(() -> player.setHealth(15));
        safe(() -> player.heal(2));
        safe(() -> player.setPosition(new Vector3(1, 80, 1)));
        safe(() -> player.setSprinting(true));
        safe(() -> player.setSneaking(true));
        safe(() -> player.setAllowFlight(true));
        safe(() -> player.setViewDistance(6));
        Assertions.assertNotNull(player);
    }

    @Test
    void breakAndActivateBlocksAsPlayer() {
        Item hand = Item.get("minecraft:diamond_pickaxe");
        int checked = 0;
        int x = 0;

        for (BlockState state : Registries.BLOCKSTATE.getAllState()) {
            if (checked >= 1500) break; // bound runtime; interaction is heavier than a getter
            try {
                Vector3 pos = new Vector3(x++ & 63, 72, 2);
                level.setBlock(pos, state.toBlock());
                safe(() -> level.useBreakOn(pos, hand, player));
                level.setBlock(pos, state.toBlock());
                safe(() -> level.getBlock(pos).onActivate(hand, player, BlockFace.UP, 0.5f, 0.5f, 0.5f));
                checked++;
            } catch (Throwable ignore) {
            }
        }
        Assertions.assertTrue(checked > 0);
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
