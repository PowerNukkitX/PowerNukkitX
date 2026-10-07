package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockDarkOakLeaves;
import org.powernukkitx.block.BlockDarkOakLog;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Roofed-tree geometry used by vanilla world generation.
 *
 * @author Curse
 */
public final class ObjectRoofedTreeWorldgen extends ObjectGenerator {

    private static final int TRUNK_WIDTH = 2;
    private static final int[] DIRECTION_X = {0, -1, 0, 1};
    private static final int[] DIRECTION_Z = {1, 0, -1, 0};

    private static final BlockState LOG = BlockDarkOakLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
    );
    private static final BlockState LEAVES = BlockDarkOakLeaves.PROPERTIES.getDefaultState();
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    private static final BlockState VINE_WEST = vine(BlockFace.EAST);
    private static final BlockState VINE_EAST = vine(BlockFace.WEST);
    private static final BlockState VINE_NORTH = vine(BlockFace.SOUTH);
    private static final BlockState VINE_SOUTH = vine(BlockFace.NORTH);

    private final boolean vines;

    /**
     * Creates the configured roofed-tree variant.
     *
     * @param vines whether the native trunk-vine decorator is enabled
     */
    public ObjectRoofedTreeWorldgen(boolean vines) {
        this.vines = vines;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        int treeHeight = 6 + random.nextExclusiveInt(3) + random.nextExclusiveInt(2);
        if (y <= level.getMinHeight() || y + treeHeight >= level.getMaxHeight()) {
            return false;
        }

        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))
                || !hasClearance(level, x, y, z, treeHeight)) {
            return false;
        }

        fixBase(level, x, y - 1, z);

        int direction = random.nextExclusiveInt(4);
        int leanHeight = random.nextExclusiveInt(4);
        int leanSteps = random.nextExclusiveInt(3);
        int leanStart = treeHeight - leanHeight;

        int trunkX = x;
        int trunkZ = z;
        int remainingLeanSteps = leanSteps;
        boolean placedTrunk = false;

        for (int offsetY = 0; offsetY < treeHeight; offsetY++) {
            if (offsetY >= leanStart && remainingLeanSteps > 0) {
                trunkX += DIRECTION_X[direction];
                trunkZ += DIRECTION_Z[direction];
                remainingLeanSteps--;
            }

            int trunkY = y + offsetY;
            for (int offsetX = 0; offsetX < TRUNK_WIDTH; offsetX++) {
                for (int offsetZ = 0; offsetZ < TRUNK_WIDTH; offsetZ++) {
                    int blockX = trunkX + offsetX;
                    int blockZ = trunkZ + offsetZ;

                    if (!placeLog(level, blockX, trunkY, blockZ)) {
                        continue;
                    }

                    placedTrunk = true;
                    if (this.vines) {
                        decorateTrunkLog(level, random, blockX, trunkY, blockZ, offsetX, offsetZ);
                    }
                }
            }
        }

        if (!placedTrunk) {
            return false;
        }

        int topY = y + treeHeight - 1;
        placeMainCanopy(level, random, trunkX, topY, trunkZ);
        placeBranches(level, random, x, z, trunkX, topY, trunkZ);
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

    private static void placeBranches(
            BlockManager level,
            RandomSourceProvider random,
            int originX,
            int originZ,
            int trunkX,
            int topY,
            int trunkZ
    ) {
        for (int offsetX = -1; offsetX <= TRUNK_WIDTH; offsetX++) {
            for (int offsetZ = -1; offsetZ <= TRUNK_WIDTH; offsetZ++) {
                if (offsetX >= 0 && offsetX < TRUNK_WIDTH && offsetZ >= 0 && offsetZ < TRUNK_WIDTH) {
                    continue;
                }

                if (random.nextExclusiveInt(3) != 0) {
                    continue;
                }

                int branchLength = 2 + random.nextExclusiveInt(3);
                int branchY = topY - 1;

                for (int index = 0; index < branchLength; index++) {
                    placeLog(level, originX + offsetX, branchY - index, originZ + offsetZ);
                }

                placeBranchCanopy(
                        level,
                        trunkX + offsetX,
                        branchY,
                        trunkZ + offsetZ
                );
            }
        }
    }

    private static void placeMainCanopy(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        for (int offsetX = -2; offsetX <= 0; offsetX++) {
            for (int offsetZ = -2; offsetZ <= 0; offsetZ++) {
                placeLeaf(level, x + offsetX, y - 1, z + offsetZ);
                placeLeaf(level, x + 1 - offsetX, y - 1, z + offsetZ);
                placeLeaf(level, x + offsetX, y - 1, z + 1 - offsetZ);
                placeLeaf(level, x + 1 - offsetX, y - 1, z + 1 - offsetZ);

                if ((offsetX > -2 || offsetZ > -1) && (offsetX != -1 || offsetZ != -2)) {
                    placeLeaf(level, x + offsetX, y + 1, z + offsetZ);
                    placeLeaf(level, x + 1 - offsetX, y + 1, z + offsetZ);
                    placeLeaf(level, x + offsetX, y + 1, z + 1 - offsetZ);
                    placeLeaf(level, x + 1 - offsetX, y + 1, z + 1 - offsetZ);
                }
            }
        }

        if (random.nextBoolean()) {
            placeLeaf(level, x, y + 2, z);
            placeLeaf(level, x + 1, y + 2, z);
            placeLeaf(level, x + 1, y + 2, z + 1);
            placeLeaf(level, x, y + 2, z + 1);
        }

        for (int offsetX = -3; offsetX <= 4; offsetX++) {
            for (int offsetZ = -3; offsetZ <= 4; offsetZ++) {
                boolean outerCorner = (offsetX == -3 || offsetX == 4)
                        && (offsetZ == -3 || offsetZ == 4);
                if (!outerCorner && (Math.abs(offsetX) < 3 || Math.abs(offsetZ) < 3)) {
                    placeLeaf(level, x + offsetX, y, z + offsetZ);
                }
            }
        }
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

    private void decorateTrunkLog(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int offsetX,
            int offsetZ
    ) {
        if (offsetX == 0) {
            placeVine(level, random, x - 1, y, z, VINE_WEST);
        }
        if (offsetX == TRUNK_WIDTH - 1) {
            placeVine(level, random, x + 1, y, z, VINE_EAST);
        }
        if (offsetZ == 0) {
            placeVine(level, random, x, y, z - 1, VINE_NORTH);
        }
        if (offsetZ == TRUNK_WIDTH - 1) {
            placeVine(level, random, x, y, z + 1, VINE_SOUTH);
        }
    }

    private static void placeVine(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            BlockState vine
    ) {
        if (random.nextExclusiveInt(7) >= 6) {
            return;
        }
        if (BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private static boolean placeLog(BlockManager level, int x, int y, int z) {
        if (!mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            return false;
        }

        level.setBlockStateAt(x, y, z, LOG);
        return true;
    }

    private static void placeLeaf(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return;
        }
        if (mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, LEAVES);
        }
    }

    private static void fixBase(BlockManager level, int x, int y, int z) {
        for (int offsetX = 0; offsetX < TRUNK_WIDTH; offsetX++) {
            for (int offsetZ = 0; offsetZ < TRUNK_WIDTH; offsetZ++) {
                int blockX = x + offsetX;
                int blockZ = z + offsetZ;
                String id = level.getBlockIdIfCachedOrLoaded(blockX, y, blockZ);

                if (!BlockID.DIRT.equals(id) && !BlockID.COARSE_DIRT.equals(id)) {
                    level.setBlockStateAtUnchecked(blockX, y, blockZ, DIRT);
                }
            }
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
