package org.powernukkitx.entity.ai.route.posevaluator;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * {@link IPosEvaluator} are used by {@link org.powernukkitx.entity.ai.route.finder.IRouteFinder IRouteFinder} to evaluate coordinates<br>
 * By writing specific coordinate {@link IPosEvaluator}, {@link org.powernukkitx.entity.ai.route.finder.IRouteFinder IRouteFinder}'s pathfinding strategy can be customized
 */


public interface IPosEvaluator {
    /**
     * Returns whether the target coordinates can be used as a waypoint, usually used for entities that return non-integer coordinates (flying and swimming)<br>
     * Only need to implement this method if the pathfinder using this evaluator returns non-integer coordinates.
     *
     * @param entity the target entity
     * @param pos    the coordinate to evaluate
     * @return whether it can be used as a waypoint
     */
    default boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 pos) {
        return true;
    }

    /**
     * Returns whether this block can be used as a standing block, typically used for entities that return integer coordinates (walking)<br>
     * Only need to implement this if the pathfinder using this evaluator only returns integer coordinates.
     *
     * @param entity the target entity
     * @param block  the block to evaluate
     * @return whether it can be used as a block to stand on
     */
    default boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        return true;
    }
}
