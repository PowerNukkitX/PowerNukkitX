package org.powernukkitx.level.generator.object;

import com.google.common.base.Preconditions;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockPodzol;
import org.powernukkitx.block.BlockSpruceLeaves;
import org.powernukkitx.block.BlockSpruceLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Mega spruce/pine tree geometry.
 *
 * @author Curse
 */
public final class ObjectMegaConiferTree extends ObjectGenerator {

    private static final int TRUNK_WIDTH = 2;
    private static final int BASE_CLUSTER_COUNT = 5;
    private static final int BASE_CLUSTER_RADIUS = 2;
    private static final float RADIUS_STEP_MODIFIER = 3.5f;

    private static final BlockState LOG = BlockSpruceLog.PROPERTIES.getDefaultState();
    private static final BlockState LEAVES = BlockSpruceLeaves.PROPERTIES.getDefaultState();
    private static final BlockState PODZOL = BlockPodzol.PROPERTIES.getDefaultState();

    private final int canopyHeightMin;
    private final int canopyHeightMax;

    /**
     * Creates a mega conifer with a max-exclusive canopy-height range.
     */
    public ObjectMegaConiferTree(int canopyHeightMin, int canopyHeightMax) {
        Preconditions.checkArgument(canopyHeightMin >= 0, "canopyHeightMin must be >= 0");
        Preconditions.checkArgument(canopyHeightMax > canopyHeightMin, "canopyHeightMax must be > canopyHeightMin");
        this.canopyHeightMin = canopyHeightMin;
        this.canopyHeightMax = canopyHeightMax;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        int treeHeight = 13 + random.nextExclusiveInt(3) + random.nextExclusiveInt(15);
        if (y <= level.getMinHeight() || y + treeHeight >= level.getMaxHeight()) {
            return false;
        }
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }
        if (!hasClearance(level, x, y, z, treeHeight)) {
            return false;
        }

        placeCanopy(level, random, x, y + treeHeight, z);
        placeTrunk(level, x, y, z, treeHeight);
        placeBaseFootprint(level, x, y - 1, z);
        placeBaseClusters(level, random, x, y - 1, z);
        return true;
    }

    private void placeCanopy(BlockManager level, RandomSourceProvider random, int x, int y, int z) {
        int canopyHeight = canopyHeightMin + random.nextExclusiveInt(canopyHeightMax - canopyHeightMin);
        int previousPreRadius = 0;

        for (int offsetY = -canopyHeight; offsetY <= 0; offsetY++) {
            int leafY = y + offsetY;
            int preRadius;

            if (offsetY == 0) {
                preRadius = 0;
            } else {
                float ratio = (float) -offsetY / (float) canopyHeight;
                preRadius = (int) Math.floor(ratio * RADIUS_STEP_MODIFIER);
            }

            int radius = preRadius;
            if (offsetY != 0 && preRadius == previousPreRadius && (leafY & 1) == 0) {
                radius++;
            }

            placeLeafLayer(level, x, leafY, z, radius);
            previousPreRadius = preRadius;
        }
    }

    private static void placeLeafLayer(BlockManager level, int x, int y, int z, int radius) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return;
        }

        int radiusSquared = radius * radius;
        int max = TRUNK_WIDTH - 1 + radius;

        for (int offsetX = -radius; offsetX <= max; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= max; offsetZ++) {
                if (!insideRadialLayer(offsetX, offsetZ, radiusSquared)) {
                    continue;
                }

                int leafX = x + offsetX;
                int leafZ = z + offsetZ;
                if (mayReplace(level.getBlockIdIfCachedOrLoaded(leafX, y, leafZ))) {
                    level.setBlockStateAt(leafX, y, leafZ, LEAVES);
                }
            }
        }
    }

    private static boolean insideRadialLayer(int x, int z, int radiusSquared) {
        return squaredDistance(x, z) <= radiusSquared
                || squaredDistance(x - 1, z) <= radiusSquared
                || squaredDistance(x, z - 1) <= radiusSquared
                || squaredDistance(x - 1, z - 1) <= radiusSquared;
    }

    private static int squaredDistance(int x, int z) {
        return x * x + z * z;
    }

    private static void placeTrunk(BlockManager level, int x, int y, int z, int treeHeight) {
        for (int offsetY = 0; offsetY < treeHeight - 1; offsetY++) {
            for (int offsetX = 0; offsetX < TRUNK_WIDTH; offsetX++) {
                for (int offsetZ = 0; offsetZ < TRUNK_WIDTH; offsetZ++) {
                    placeLog(level, x + offsetX, y + offsetY, z + offsetZ);
                }
            }
        }

        placeLog(level, x, y + treeHeight - 1, z);
    }

    private static void placeLog(BlockManager level, int x, int y, int z) {
        if (mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, LOG);
        }
    }

    private static void placeBaseFootprint(BlockManager level, int x, int y, int z) {
        for (int offsetX = 0; offsetX < TRUNK_WIDTH; offsetX++) {
            for (int offsetZ = 0; offsetZ < TRUNK_WIDTH; offsetZ++) {
                if (!BlockID.PODZOL.equals(level.getBlockIdIfCachedOrLoaded(x + offsetX, y, z + offsetZ))) {
                    level.setBlockStateAt(x + offsetX, y, z + offsetZ, PODZOL);
                }
            }
        }
    }

    private static void placeBaseClusters(BlockManager level, RandomSourceProvider random, int x, int y, int z) {
        placeBaseCircle(level, x - 1, y, z - 1);
        placeBaseCircle(level, x + TRUNK_WIDTH, y, z - 1);
        placeBaseCircle(level, x - 1, y, z + TRUNK_WIDTH);
        placeBaseCircle(level, x + TRUNK_WIDTH, y, z + TRUNK_WIDTH);

        for (int i = 0; i < BASE_CLUSTER_COUNT; i++) {
            int value = random.nextExclusiveInt(64);
            int quotient = value / 8;
            int remainder = value % 8;

            if (quotient == 0 || quotient == 7 || remainder == 0 || remainder == 7) {
                placeBaseCircle(level, x + remainder - 3, y, z + quotient - 3);
            }
        }
    }

    private static void placeBaseCircle(BlockManager level, int x, int y, int z) {
        for (int offsetX = -BASE_CLUSTER_RADIUS; offsetX <= BASE_CLUSTER_RADIUS; offsetX++) {
            for (int offsetZ = -BASE_CLUSTER_RADIUS; offsetZ <= BASE_CLUSTER_RADIUS; offsetZ++) {
                if (Math.abs(offsetX) == BASE_CLUSTER_RADIUS && Math.abs(offsetZ) == BASE_CLUSTER_RADIUS) {
                    continue;
                }
                replaceBaseBlock(level, x + offsetX, y, z + offsetZ);
            }
        }
    }

    private static void replaceBaseBlock(BlockManager level, int x, int y, int z) {
        for (int offsetY = 2; offsetY >= -3; offsetY--) {
            int blockY = y + offsetY;
            String id = level.getBlockIdIfCachedOrLoaded(x, blockY, z);

            if (BlockID.PODZOL.equals(id)) {
                return;
            }
            if (mayReplaceWithPodzol(id)) {
                level.setBlockStateAt(x, blockY, z, PODZOL);
                return;
            }
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
            int blockY = y + offsetY;
            if (blockY < level.getMinHeight() || blockY >= level.getMaxHeight()) {
                return false;
            }

            int radius = offsetY == 0 ? TRUNK_WIDTH - 1 : TRUNK_WIDTH;
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

    private static boolean mayReplaceWithPodzol(String id) {
        return BlockID.GRASS_BLOCK.equals(id)
                || BlockID.DIRT.equals(id)
                || BlockID.COARSE_DIRT.equals(id);
    }
}
