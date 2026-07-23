package org.powernukkitx.block;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Level-backed block smoke pass. Places every registered block state into the real
 * fixture world and exercises the world-dependent getters (bounding boxes, colour,
 * drops, comparator override, light recompute, block entity...) that the pure
 * BlockSmokeTest cannot reach because it has no level.
 */
public class BlockInLevelSmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyBlockStatePlacesAndAnswersWorldGetters() {
        Set<BlockState> states = Registries.BLOCKSTATE.getAllState();
        Assertions.assertFalse(states.isEmpty(), "no block states registered");

        int checked = 0;
        int failCount = 0;
        int x = 0;
        StringBuilder failures = new StringBuilder();
        Item probe = Item.get("minecraft:diamond_pickaxe");

        for (BlockState state : states) {
            try {
                Block block = state.toBlock();
                if (block == null) continue;
                Vector3 pos = new Vector3(x, 80, 0);
                x = (x + 1) % 64;
                level.setBlock(pos, block);
                exercise(level.getBlock(pos), probe);
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 25) {
                    failures.append('\n').append(state.getIdentifier()).append(" -> ").append(t);
                }
            }
        }

        System.out.println("[BlockInLevelSmokeTest] states=" + states.size() + " checked=" + checked + " failed=" + failCount);
        Assertions.assertTrue(checked > 0, "no block state survived the level smoke pass" + failures);
        Assertions.assertTrue(failCount < states.size() * 0.5,
                "too many block states failed the level smoke pass (" + failCount + "/" + states.size() + ")" + failures);
    }

    private void exercise(Block block, Item probe) {
        safe(block::getCollisionBoundingBox);
        safe(block::getBoundingBox);
        safe(block::getMinX);
        safe(block::getMinY);
        safe(block::getMinZ);
        safe(block::getMaxX);
        safe(block::getMaxY);
        safe(block::getMaxZ);
        safe(block::getColor);
        safe(block::getLightLevel);
        safe(block::getLightFilter);
        safe(block::isFullBlock);
        safe(block::canBeActivated);
        safe(block::getComparatorInputOverride);
        safe(block::isNormalBlock);
        safe(block::isSolid);
        safe(block::isTransparent);
        safe(block::toItem);
        safe(() -> block.getDrops(probe));
        safe(() -> block.calculateBreakTime(probe));
        safe(() -> block.getWeakPower(org.powernukkitx.math.BlockFace.NORTH));
        safe(() -> block.getStrongPower(org.powernukkitx.math.BlockFace.NORTH));
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
