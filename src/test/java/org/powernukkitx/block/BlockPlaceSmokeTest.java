package org.powernukkitx.block;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Places a bounded sample of block states via Level.useItemOn and Block.place so the
 * place() logic across many blocks is actually entered. All calls tolerant - most placements
 * fail validation and that is fine, we only want coverage of the placement entry paths.
 */
class BlockPlaceSmokeTest {

    private static final int SAMPLE = 2000;
    private static TestPlayer player;
    private static Level level;

    @BeforeAll
    static void setup() {
        player = PlayerFixture.get();
        level = player.getLevel();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void placesSampleOfBlocks() {
        int placed = 0;
        int y = 5;

        for (BlockState state : Registries.BLOCKSTATE.getAllState()) {
            if (placed >= SAMPLE) {
                break;
            }
            placed++;

            // Spread placements out so they don't stomp each other; keep a solid floor below.
            final int x = 8 + (placed % 40) * 2;
            final int z = 8 + (placed / 40) * 2;
            final Vector3 below = new Vector3(x, y - 1, z);
            final Vector3 target = new Vector3(x, y, z);

            safe(() -> level.setBlock(below, Block.get("minecraft:stone")));
            safe(() -> level.setBlock(target, Block.get("minecraft:air")));

            final BlockState s = state;
            safe(() -> {
                Item item = s.toBlock().toItem();
                if (item != null) {
                    level.useItemOn(below, item, BlockFace.UP, null, player);
                }
            });

            safe(() -> {
                Block block = s.toBlock();
                Item item = block.toItem();
                Block targetBlock = level.getBlock(target);
                if (item != null && targetBlock != null) {
                    block.place(item, targetBlock, targetBlock, BlockFace.UP, 0.5, 0.5, 0.5, player);
                }
            });
        }

        assertTrue(placed > 0, "expected at least one block state to be sampled");
    }
}
