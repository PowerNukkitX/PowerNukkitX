package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;

/**
 * Resolves one tree candidate position.
 *
 * @author Curse
 */
@FunctionalInterface
public interface TreeCandidatePolicy {

    /**
     * Selects a candidate relative to the supplied rule origin.
     */
    boolean select(FeaturePlacementContext context, int originX, int originZ);

    /**
     * Creates the vanilla legacy surface-tree candidate policy.
     */
    static TreeCandidatePolicy legacySurface() {
        return (context, originX, originZ) -> {
            IChunk chunk = context.getGenerationContext().getChunk();
            Level level = context.getGenerationContext().getLevel();

            int z = originZ + context.getRandom().nextExclusiveInt(14) + 1;
            int x = originX + context.getRandom().nextExclusiveInt(14) + 1;

            int localX = x - (chunk.getX() << 4);
            int localZ = z - (chunk.getZ() << 4);
            int y = chunk.getHeightMap(localX, localZ);

            if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
                return false;
            }

            context.setPosition(x, y, z);
            return true;
        };
    }
}
