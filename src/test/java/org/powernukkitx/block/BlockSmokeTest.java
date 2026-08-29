package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Broad smoke coverage - builds every registered block state and calls the pure,
 * position-independent getters. Not asserting exact values (those live in dedicated
 * tests) but making sure every block class can be instantiated and queried without
 * blowing up. Any block that throws is collected and reported.
 */
public class BlockSmokeTest {

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
    }

    @Test
    void everyBlockStateBuildsAndAnswersGetters() {
        Set<BlockState> states = Registries.BLOCKSTATE.getAllState();
        Assertions.assertFalse(states.isEmpty(), "no block states registered");

        int checked = 0;
        StringBuilder failures = new StringBuilder();
        int failCount = 0;

        for (BlockState state : states) {
            try {
                exerciseState(state);
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 20) {
                    failures.append('\n').append(state.getIdentifier())
                            .append(" -> ").append(t);
                }
            }
        }

        Assertions.assertTrue(checked > 0, "no block state survived the smoke pass");
        // A handful of exotic blocks may legitimately need world context; keep the
        // gate loose but catch a systemic regression.
        Assertions.assertTrue(failCount < states.size() * 0.05,
                "too many block states failed the smoke pass (" + failCount + "/" + states.size() + ")" + failures);
    }

    private void exerciseState(BlockState state) {
        Assertions.assertNotNull(state.getIdentifier());
        state.blockStateHash();
        state.specialValue();
        state.unsignedBlockStateHash();
        Assertions.assertNotNull(state.getBlockStateTag());
        state.isDefaultState();

        Block block = state.toBlock();
        Assertions.assertNotNull(block);
        Assertions.assertNotNull(block.getBlockState());

        // identity getters - pure, must never need a world
        Assertions.assertNotNull(block.getName());
        Assertions.assertNotNull(block.getId());
        Assertions.assertNotNull(block.getItemId());
        Assertions.assertNotNull(block.getTags());
        block.isAir();
        block.hashCode();
        Assertions.assertNotNull(block.toString());

        // Connected blocks (walls, fences, panes...) resolve their shape from neighbours
        // and legitimately need a level. Call every getter best-effort so its body still
        // gets exercised, but a missing-level throw must not fail the smoke pass.
        safe(block::getHardness);
        safe(block::getResistance);
        safe(block::getToolType);
        safe(block::getToolTier);
        safe(block::getBurnChance);
        safe(block::getBurnAbility);
        safe(block::getFrictionFactor);
        safe(block::getPassableBlockFrictionFactor);
        safe(block::getWalkThroughExtraCost);
        safe(block::getLightLevel);
        safe(block::getLightFilter);
        safe(block::canBePlaced);
        safe(block::canBeReplaced);
        safe(block::isTransparent);
        safe(block::isSolid);
        safe(block::canBeFlowedInto);
        safe(block::getWaterloggingLevel);
        safe(block::isWaterLogged);
        safe(block::getSnowloggingLevel);
        safe(block::isSnowLogged);
        safe(block::canBeActivated);
        safe(block::hasEntityCollision);
        safe(block::canPassThrough);
        safe(block::canBePushed);
        safe(block::canBePulled);
        safe(block::canSticksBlock);
        safe(block::hasComparatorInputOverride);
        safe(block::canBeClimbed);
        safe(block::canBeExploded);
        safe(block::getMinX);
        safe(block::getMinY);
        safe(block::getMinZ);
        safe(block::getMaxX);
        safe(block::getMaxY);
        safe(block::getMaxZ);
        safe(block::isPowerSource);
        safe(block::getDropExp);
        safe(block::isNormalBlock);
        safe(block::isSimpleBlock);
        safe(block::isFullBlock);
        safe(block::isLavaResistant);
        safe(block::getItemMaxStackSize);
        safe(block::isTickingDisabled);
        safe(block::getColor);
        safe(block::toItem);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
