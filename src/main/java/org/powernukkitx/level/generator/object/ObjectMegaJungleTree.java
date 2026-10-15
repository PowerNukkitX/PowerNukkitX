package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockJungleLeaves;
import org.powernukkitx.block.BlockJungleLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.MathHelper;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Mega-jungle tree geometry used by vanilla world generation.
 *
 * @author Curse
 */
public final class ObjectMegaJungleTree extends ObjectGenerator {

    private static final int TRUNK_WIDTH = 2;
    private static final int BRANCH_LENGTH = 5;
    private static final float BRANCH_SLOPE = 0.5f;

    private static final BlockState LOG = BlockJungleLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
    );
    private static final BlockState LEAVES = BlockJungleLeaves.PROPERTIES.getDefaultState();
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    private static final BlockState VINE_WEST = vine(BlockFace.EAST);
    private static final BlockState VINE_EAST = vine(BlockFace.WEST);
    private static final BlockState VINE_NORTH = vine(BlockFace.SOUTH);
    private static final BlockState VINE_SOUTH = vine(BlockFace.NORTH);

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        int treeHeight = 10 + random.nextExclusiveInt(3) + random.nextExclusiveInt(20);
        if (y <= level.getMinHeight() || y + treeHeight >= level.getMaxHeight()) {
            return false;
        }
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }
        if (!hasClearance(level, x, y, z, treeHeight)) {
            return false;
        }

        placeMegaCanopy(level, x, y + treeHeight, z, 3, 2, 2, false);
        placeBranches(level, random, x, y, z, treeHeight);
        placeTrunk(level, random, x, y, z, treeHeight);
        placeBaseFootprint(level, x, y - 1, z);
        return true;
    }

    private static boolean hasClearance(BlockManager level, int x, int y, int z, int treeHeight) {
        for (int offsetY = 0; offsetY <= treeHeight + 1; offsetY++) {
            int blockY = y + offsetY;
            if (blockY < level.getMinHeight() || blockY >= level.getMaxHeight()) {
                return false;
            }

            int radius = offsetY == 0 ? TRUNK_WIDTH - 1 : TRUNK_WIDTH;
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    if (!mayGrowThrough(level.getBlockIdIfCachedOrLoaded(x + offsetX, blockY, z + offsetZ))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeBranches(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int treeHeight
    ) {
        int branchY = y + treeHeight - (2 + random.nextExclusiveInt(4));
        int minBranchY = y + (int) (treeHeight * 0.5f);

        while (branchY > minBranchY) {
            float angle = random.nextFloat() * (float) Math.PI;
            angle += angle;

            int baseY = (int) ((float) (branchY - 1) - (BRANCH_LENGTH - 1) * BRANCH_SLOPE);
            int branchX = x;
            int branchZ = z;

            for (int index = 0; index < BRANCH_LENGTH; index++) {
                float distance = index;
                branchX = x + (int) (MathHelper.cos(angle) * distance + 1.5f);
                branchZ = z + (int) (MathHelper.sin(angle) * distance + 1.5f);
                int branchLogY = (int) (baseY + distance * BRANCH_SLOPE);

                if (mayReplace(level.getBlockIdIfCachedOrLoaded(branchX, branchLogY, branchZ))) {
                    level.setBlockStateAt(branchX, branchLogY, branchZ, LOG);
                }
            }

            int canopyHeight = 2 + random.nextExclusiveInt(2);
            placeMegaCanopy(level, branchX, branchY, branchZ, canopyHeight, 1, 0, true);

            branchY -= 2 + random.nextExclusiveInt(4);
        }
    }

    private static void placeMegaCanopy(
            BlockManager level,
            int x,
            int y,
            int z,
            int height,
            int baseRadius,
            int coreWidth,
            boolean simplified
    ) {
        for (int countdown = height - 1; countdown >= 0; countdown--) {
            int leafY = y - countdown;
            int radius = baseRadius + coreWidth / 2 + countdown;

            if (simplified) {
                placeSimplifiedLeafLayer(level, x, leafY, z, radius);
            } else {
                placeCoreLeafLayer(level, x, leafY, z, radius, coreWidth);
            }
        }
    }

    private static void placeSimplifiedLeafLayer(BlockManager level, int x, int y, int z, int radius) {
        int radiusSquared = radius * radius;

        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                if (offsetX * offsetX + offsetZ * offsetZ > radiusSquared) {
                    continue;
                }
                placeLeaf(level, x + offsetX, y, z + offsetZ);
            }
        }
    }

    private static void placeCoreLeafLayer(
            BlockManager level,
            int x,
            int y,
            int z,
            int radius,
            int coreWidth
    ) {
        int radiusSquared = radius * radius;
        int max = coreWidth - 1 + radius;

        for (int offsetX = -radius; offsetX <= max; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= max; offsetZ++) {
                if (!insideCoreLayer(offsetX, offsetZ, radiusSquared)) {
                    continue;
                }
                placeLeaf(level, x + offsetX, y, z + offsetZ);
            }
        }
    }

    private static boolean insideCoreLayer(int x, int z, int radiusSquared) {
        return squaredDistance(x, z) <= radiusSquared
                || squaredDistance(x - 1, z) <= radiusSquared
                || squaredDistance(x, z - 1) <= radiusSquared
                || squaredDistance(x - 1, z - 1) <= radiusSquared;
    }

    private static int squaredDistance(int x, int z) {
        return x * x + z * z;
    }

    private static void placeLeaf(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return;
        }
        if (mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, LEAVES);
        }
    }

    private static void placeTrunk(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int treeHeight
    ) {
        placeLog(level, x, y, z);
        placeLog(level, x, y, z + 1);
        placeLog(level, x + 1, y, z);
        placeLog(level, x + 1, y, z + 1);

        for (int offsetY = 1; offsetY < treeHeight - 1; offsetY++) {
            int trunkY = y + offsetY;

            if (placeLog(level, x, trunkY, z)) {
                placeTrunkVine(level, random, x - 1, trunkY, z, VINE_WEST);
                placeTrunkVine(level, random, x, trunkY, z - 1, VINE_NORTH);
            }

            if (placeLog(level, x, trunkY, z + 1)) {
                placeTrunkVine(level, random, x - 1, trunkY, z + 1, VINE_WEST);
                placeTrunkVine(level, random, x, trunkY, z + 2, VINE_SOUTH);
            }

            if (placeLog(level, x + 1, trunkY, z)) {
                placeTrunkVine(level, random, x + 2, trunkY, z, VINE_EAST);
                placeTrunkVine(level, random, x + 1, trunkY, z - 1, VINE_NORTH);
            }

            if (placeLog(level, x + 1, trunkY, z + 1)) {
                placeTrunkVine(level, random, x + 2, trunkY, z + 1, VINE_EAST);
                placeTrunkVine(level, random, x + 1, trunkY, z + 2, VINE_SOUTH);
            }
        }

        placeLog(level, x, y + treeHeight - 1, z);
    }

    private static boolean placeLog(BlockManager level, int x, int y, int z) {
        if (!mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            return false;
        }
        level.setBlockStateAt(x, y, z, LOG);
        return true;
    }

    private static void placeTrunkVine(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            BlockState vine
    ) {
        if (random.nextExclusiveInt(3) != 0) {
            return;
        }
        if (BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private static void placeBaseFootprint(BlockManager level, int x, int y, int z) {
        for (int offsetX = 0; offsetX < TRUNK_WIDTH; offsetX++) {
            for (int offsetZ = 0; offsetZ < TRUNK_WIDTH; offsetZ++) {
                int blockX = x + offsetX;
                int blockZ = z + offsetZ;

                if (!BlockID.DIRT.equals(level.getBlockIdIfCachedOrLoaded(blockX, y, blockZ))) {
                    level.setBlockStateAt(blockX, y, blockZ, DIRT);
                }
            }
        }
    }

    private static boolean mayGrowOn(String id) {
        return switch (id) {
            case BlockID.GRASS_BLOCK,
                 BlockID.DIRT,
                 BlockID.PODZOL,
                 BlockID.COARSE_DIRT -> true;
            default -> false;
        };
    }

    private static boolean mayGrowThrough(String id) {
        return BlockID.DIRT.equals(id)
                || BlockID.GRASS_BLOCK.equals(id)
                || BlockID.COARSE_DIRT.equals(id)
                || mayReplace(id);
    }

    private static boolean mayReplace(String id) {
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

    private static BlockState vine(BlockFace supportFace) {
        return BlockVine.PROPERTIES.getBlockState(
                CommonBlockProperties.VINE_DIRECTION_BITS,
                BlockVine.getMetaFromFace(supportFace)
        );
    }
}
