package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.BlockCocoa;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Jungle cocoa placement.
 *
 * @author Curse
 */
final class JungleCocoaPlacementFeature implements PlacementFeature {
    private static final int[] SITE_X = {0, 1, 2, 0};
    private static final int[] SITE_Z = {1, 0, 0, -1};

    @Override
    public boolean place(FeaturePlacementContext context) {
        RandomSourceProvider random = context.getRandom();
        if (random.nextExclusiveInt(5) != 0) {
            return false;
        }

        BlockManager level = context.getRoot();
        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        boolean placed = placeLayer(level, random, x, y - 5, z, 4);
        if (placeLayer(level, random, x, y - 4, z, 3)) {
            placed = true;
        }
        return placed;
    }

    private static boolean placeLayer(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int chanceDenominator
    ) {
        boolean placed = false;

        for (int index = 0; index < SITE_X.length; index++) {
            if (random.nextExclusiveInt(chanceDenominator) != 0) {
                continue;
            }

            if (placeCocoa(level, random, x + SITE_X[index], y, z + SITE_Z[index])) {
                placed = true;
            }
        }

        return placed;
    }

    private static boolean placeCocoa(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        int age = random.nextExclusiveInt(3);

        // SingleBlockFeature still performs its one-entry weighted selection.
        random.nextExclusiveInt(1);

        BlockFace support = null;

        if (isJungleLog(level, x, y, z - 1)) {
            support = BlockFace.NORTH;
        }
        if (isJungleLog(level, x + 1, y, z)) {
            support = BlockFace.EAST;
        }
        if (isJungleLog(level, x, y, z + 1)) {
            support = BlockFace.SOUTH;
        }
        if (isJungleLog(level, x - 1, y, z)) {
            support = BlockFace.WEST;
        }

        if (support == null) {
            return false;
        }

        BlockState cocoa = BlockCocoa.PROPERTIES.getBlockState(
                CommonBlockProperties.AGE_3.createValue(age),
                CommonBlockProperties.DIRECTION.createValue(support.getHorizontalIndex())
        );
        level.setBlockStateAt(x, y, z, cocoa);
        return true;
    }

    private static boolean isJungleLog(BlockManager level, int x, int y, int z) {
        return BlockID.JUNGLE_LOG.equals(level.getBlockIdIfCachedOrLoaded(x, y, z));
    }
}
