package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAcaciaLeaves;
import org.powernukkitx.block.BlockAcaciaLog;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Savanna tree geometry used by vanilla world generation.
 *
 * @author Curse
 */
public final class ObjectSavannaTreeWorldgen extends ObjectGenerator {

    private static final int[] DIRECTION_X = {0, -1, 0, 1};
    private static final int[] DIRECTION_Z = {1, 0, -1, 0};

    private static final BlockState LOG = BlockAcaciaLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
    );
    private static final BlockState LEAVES = BlockAcaciaLeaves.PROPERTIES.getDefaultState();
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        int treeHeight = 5 + random.nextExclusiveInt(3) + random.nextExclusiveInt(3);
        if (y <= level.getMinHeight() || y + treeHeight + 1 >= level.getMaxHeight()) {
            return false;
        }

        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))
                || !hasClearance(level, x, y, z, treeHeight)) {
            return false;
        }

        fixBase(level, x, y - 1, z);

        int mainDirection = random.nextExclusiveInt(4);
        int leanHeight = 1 + random.nextExclusiveInt(4);
        int leanSteps = 1 + random.nextExclusiveInt(3);
        int leanStart = treeHeight - leanHeight;

        int trunkX = x;
        int trunkZ = z;
        int remainingLeanSteps = leanSteps;
        int lastTrunkX = 0;
        int lastTrunkY = Integer.MIN_VALUE;
        int lastTrunkZ = 0;

        for (int offsetY = 0; offsetY < treeHeight; offsetY++) {
            if (offsetY >= leanStart && remainingLeanSteps > 0) {
                trunkX += DIRECTION_X[mainDirection];
                trunkZ += DIRECTION_Z[mainDirection];
                remainingLeanSteps--;
            }

            int trunkY = y + offsetY;
            if (placeLog(level, trunkX, trunkY, trunkZ)) {
                lastTrunkX = trunkX;
                lastTrunkY = trunkY;
                lastTrunkZ = trunkZ;
            }
        }

        if (lastTrunkY == Integer.MIN_VALUE) {
            return false;
        }

        placeMainCanopy(level, lastTrunkX, lastTrunkY, lastTrunkZ);
        placeBranch(level, random, x, y, z, treeHeight, leanHeight, mainDirection);
        return true;
    }

    private static boolean hasClearance(
            BlockManager level,
            int x,
            int y,
            int z,
            int treeHeight
    ) {
        for (int offsetY = 0; offsetY <= treeHeight + 1; offsetY++) {
            int blockY = y + offsetY;
            int radius;

            if (offsetY == 0) {
                radius = 0;
            } else if (offsetY >= treeHeight - 1) {
                radius = 2;
            } else {
                radius = 1;
            }

            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    Block block = level.getBlockIfCachedOrLoaded(x + offsetX, blockY, z + offsetZ);
                    if (!mayGrowThrough(block)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeBranch(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int treeHeight,
            int leanHeight,
            int mainDirection
    ) {
        int branchDirection = random.nextExclusiveInt(4);
        if (branchDirection == mainDirection) {
            return;
        }

        // branch_chance is 100%, so takes the fast path without consuming RNG.
        int branchPosition = 1 + random.nextExclusiveInt(2);
        int branchLength = 1 + random.nextExclusiveInt(3);
        int branchY = treeHeight - leanHeight - branchPosition;
        int branchX = x;
        int branchZ = z;

        int lastBranchX = 0;
        int lastBranchY = Integer.MIN_VALUE;
        int lastBranchZ = 0;

        while (branchY < treeHeight && branchLength > 0) {
            if (branchY > 0) {
                branchX += DIRECTION_X[branchDirection];
                branchZ += DIRECTION_Z[branchDirection];

                int worldY = y + branchY;
                if (placeLog(level, branchX, worldY, branchZ)) {
                    lastBranchX = branchX;
                    lastBranchY = worldY;
                    lastBranchZ = branchZ;
                }
            }

            branchY++;
            branchLength--;
        }

        if (lastBranchY != Integer.MIN_VALUE) {
            placeBranchCanopy(level, lastBranchX, lastBranchY, lastBranchZ);
        }
    }

    private static void placeMainCanopy(BlockManager level, int x, int y, int z) {
        placeLeafSquare(level, x, y + 1, z, 1, false);
        placeLeaf(level, x - 2, y + 1, z);
        placeLeaf(level, x + 2, y + 1, z);
        placeLeaf(level, x, y + 1, z - 2);
        placeLeaf(level, x, y + 1, z + 2);

        placeLeafSquare(level, x, y, z, 3, true);
    }

    private static void placeBranchCanopy(BlockManager level, int x, int y, int z) {
        placeLeafSquare(level, x, y + 1, z, 1, false);
        placeLeafSquare(level, x, y, z, 2, true);
    }

    private static void placeLeafSquare(
            BlockManager level,
            int x,
            int y,
            int z,
            int radius,
            boolean omitCorners
    ) {
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                if (omitCorners && Math.abs(offsetX) == radius && Math.abs(offsetZ) == radius) {
                    continue;
                }
                placeLeaf(level, x + offsetX, y, z + offsetZ);
            }
        }
    }

    private static boolean placeLog(BlockManager level, int x, int y, int z) {
        Block block = level.getBlockIfCachedOrLoaded(x, y, z);
        if (!mayReplace(block)) {
            return false;
        }

        level.setBlockStateAt(x, y, z, LOG);
        return true;
    }

    private static void placeLeaf(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return;
        }

        Block block = level.getBlockIfCachedOrLoaded(x, y, z);
        if (mayReplace(block)) {
            level.setBlockStateAt(x, y, z, LEAVES);
        }
    }

    private static void fixBase(BlockManager level, int x, int y, int z) {
        String id = level.getBlockIdIfCachedOrLoaded(x, y, z);
        if (!BlockID.DIRT.equals(id) && !BlockID.COARSE_DIRT.equals(id)) {
            level.setBlockStateAt(x, y, z, DIRT);
        }
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

    private static boolean mayGrowThrough(Block block) {
        String id = block.getId();
        return BlockID.DIRT.equals(id)
                || BlockID.GRASS_BLOCK.equals(id)
                || BlockID.COARSE_DIRT.equals(id)
                || mayReplace(block);
    }

    private static boolean mayReplace(Block block) {
        return switch (block.getId()) {
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
}
