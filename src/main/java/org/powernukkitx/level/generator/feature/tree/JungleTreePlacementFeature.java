package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockJungleLeaves;
import org.powernukkitx.block.BlockJungleLog;
import org.powernukkitx.block.BlockLeaves;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Standing jungle tree placement.
 *
 * @author Curse
 */
final class JungleTreePlacementFeature implements PlacementFeature {
    private static final BlockState LOG = BlockJungleLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
    );
    private static final BlockState LEAVES = BlockJungleLeaves.PROPERTIES.getDefaultState();
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    private static final BlockState VINE_EAST = vine(BlockFace.WEST);
    private static final BlockState VINE_NORTH = vine(BlockFace.SOUTH);
    private static final BlockState VINE_SOUTH = vine(BlockFace.NORTH);
    private static final BlockState VINE_WEST = vine(BlockFace.EAST);

    @Override
    public boolean place(FeaturePlacementContext context) {
        BlockManager level = context.getRoot();
        RandomSourceProvider random = context.getRandom();

        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        int treeHeight = 4 + random.nextExclusiveInt(7);
        if (y <= level.getMinHeight() || y + treeHeight >= level.getMaxHeight()) {
            return false;
        }
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }
        if (!hasClearance(level, x, y, z, treeHeight)) {
            return false;
        }

        int lastTrunkY = Integer.MIN_VALUE;
        for (int offset = 0; offset < treeHeight; offset++) {
            int trunkY = y + offset;
            if (!mayReplaceTrunk(level.getBlockIdIfCachedOrLoaded(x, trunkY, z))) {
                continue;
            }

            level.setBlockStateAt(x, trunkY, z, LOG);
            decorateTrunkLog(level, random, x, trunkY, z);
            lastTrunkY = trunkY;
        }

        if (lastTrunkY == Integer.MIN_VALUE) {
            return false;
        }

        int canopyY = lastTrunkY + 1;
        placeCanopy(level, random, x, canopyY, z);
        fixBase(level, x, y - 1, z);

        context.setPosition(x, canopyY, z);
        return true;
    }

    private static void placeCanopy(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        for (int offsetY = -3; offsetY <= 0; offsetY++) {
            int radius = offsetY <= -2 ? 2 : 1;

            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    boolean corner = Math.abs(offsetX) == radius && Math.abs(offsetZ) == radius;
                    if (corner && (offsetY == 0 || random.nextExclusiveInt(2) == 0)) {
                        continue;
                    }

                    int leafX = x + offsetX;
                    int leafY = y + offsetY;
                    int leafZ = z + offsetZ;
                    Block existing = level.getBlockIfCachedOrLoaded(leafX, leafY, leafZ);

                    if (isCanopyReplaceable(existing)) {
                        level.setBlockStateAt(leafX, leafY, leafZ, LEAVES);
                    }
                }
            }
        }

        decorateCanopy(level, random, x, y, z);
    }

    private static void decorateTrunkLog(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        placeTrunkVine(level, random, x - 1, y, z, VINE_WEST);
        placeTrunkVine(level, random, x + 1, y, z, VINE_EAST);
        placeTrunkVine(level, random, x, y, z - 1, VINE_NORTH);
        placeTrunkVine(level, random, x, y, z + 1, VINE_SOUTH);
    }

    private static void placeTrunkVine(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            BlockState vine
    ) {
        if (random.nextFloat() * 100.0f >= 33.33f) {
            return;
        }
        if (BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private static void decorateCanopy(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        for (int offsetY = -3; offsetY <= 0; offsetY++) {
            int radius = offsetY <= -2 ? 2 : 1;

            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    int leafX = x + offsetX;
                    int leafY = y + offsetY;
                    int leafZ = z + offsetZ;

                    if (!BlockID.JUNGLE_LEAVES.equals(level.getBlockIdIfCachedOrLoaded(leafX, leafY, leafZ))) {
                        continue;
                    }

                    placeHangingVine(level, random, leafX - 1, leafY, leafZ, VINE_WEST);
                    placeHangingVine(level, random, leafX + 1, leafY, leafZ, VINE_EAST);
                    placeHangingVine(level, random, leafX, leafY, leafZ - 1, VINE_NORTH);
                    placeHangingVine(level, random, leafX, leafY, leafZ + 1, VINE_SOUTH);
                }
            }
        }
    }

    private static void placeHangingVine(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            BlockState vine
    ) {
        if (random.nextExclusiveInt(4) != 0) {
            return;
        }

        for (int step = 0; step < 5 && BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z)); step++, y--) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private static void fixBase(BlockManager level, int x, int y, int z) {
        String id = level.getBlockIdIfCachedOrLoaded(x, y, z);
        if (!BlockID.DIRT.equals(id) && !BlockID.COARSE_DIRT.equals(id)) {
            level.setBlockStateAt(x, y, z, DIRT);
        }
    }

    private static boolean hasClearance(
            BlockManager level,
            int x,
            int y,
            int z,
            int treeHeight
    ) {
        for (int offsetY = 0; offsetY <= treeHeight + 1; offsetY++) {
            int radius;
            if (offsetY == 0) {
                radius = 0;
            } else if (offsetY >= treeHeight - 1) {
                radius = 2;
            } else {
                radius = 1;
            }

            int blockY = y + offsetY;
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    if (!mayGrowThrough(level.getBlockIdIfCachedOrLoaded(
                            x + offsetX,
                            blockY,
                            z + offsetZ
                    ))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean mayGrowOn(String id) {
        return switch (id) {
            case BlockID.DIRT,
                 BlockID.GRASS_BLOCK,
                 BlockID.PODZOL,
                 BlockID.COARSE_DIRT,
                 BlockID.FARMLAND -> true;
            default -> false;
        };
    }

    private static boolean mayGrowThrough(String id) {
        return BlockID.DIRT.equals(id)
                || BlockID.GRASS_BLOCK.equals(id)
                || BlockID.COARSE_DIRT.equals(id)
                || mayReplaceTrunk(id);
    }

    private static boolean mayReplaceTrunk(String id) {
        return switch (id) {
            case BlockID.AIR,
                 BlockID.OAK_LEAVES,
                 BlockID.SPRUCE_LEAVES,
                 BlockID.BIRCH_LEAVES,
                 BlockID.JUNGLE_LEAVES,
                 BlockID.ACACIA_LEAVES,
                 BlockID.DARK_OAK_LEAVES -> true;
            default -> false;
        };
    }

    private static boolean isCanopyReplaceable(Block block) {
        return BlockID.AIR.equals(block.getId())
                || BlockID.VINE.equals(block.getId())
                || block instanceof BlockLeaves;
    }

    private static BlockState vine(BlockFace supportFace) {
        return BlockVine.PROPERTIES.getBlockState(
                CommonBlockProperties.VINE_DIRECTION_BITS,
                BlockVine.getMetaFromFace(supportFace)
        );
    }
}
