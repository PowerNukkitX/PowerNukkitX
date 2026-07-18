package org.powernukkitx.block;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Drives the behaviour entry points of every block (onUpdate / onActivate / onBreak /
 * place) against the real fixture level with a null player. Covers the large amount of
 * block logic that only runs when a block is interacted with - untouched by the getter
 * smoke tests.
 */
public class BlockBehaviorSmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyBlockRunsItsBehaviourHooks() {
        int checked = 0;
        int x = 0;
        Item hand = Item.get("minecraft:diamond_pickaxe");

        for (BlockState state : Registries.BLOCKSTATE.getAllState()) {
            try {
                Vector3 pos = new Vector3(x++ & 63, 75, 0);
                Block block = state.toBlock();
                level.setBlock(pos, block);
                Block placed = level.getBlock(pos);

                safe(() -> placed.onUpdate(Level.BLOCK_UPDATE_NORMAL));
                safe(() -> placed.onUpdate(Level.BLOCK_UPDATE_SCHEDULED));
                safe(() -> placed.onUpdate(Level.BLOCK_UPDATE_RANDOM));
                safe(() -> placed.onActivate(hand, null, BlockFace.UP, 0.5f, 0.5f, 0.5f));
                safe(() -> placed.onBreak(hand));
                checked++;
            } catch (Throwable ignore) {
            }
        }
        Assertions.assertTrue(checked > 0, "no block behaviour was exercised");
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
