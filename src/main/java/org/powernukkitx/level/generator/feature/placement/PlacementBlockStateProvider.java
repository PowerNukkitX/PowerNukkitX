package org.powernukkitx.level.generator.feature.placement;

import org.powernukkitx.block.BlockState;

/**
 * Produces the block state for one vegetation placement attempt.
 *
 * @author Curse
 */
@FunctionalInterface
public interface PlacementBlockStateProvider {

    /**
     * Produces the state for the current placement attempt.
     */
    BlockState get(FeaturePlacementContext context);
}
