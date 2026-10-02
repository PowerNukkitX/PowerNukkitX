package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSpruceLeaves;
import org.powernukkitx.block.BlockSpruceLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Pine tree feature.
 *
 * @author Curse
 */
public final class ObjectPineTree extends ObjectGenerator {
    private static final BlockState LOG = BlockSpruceLog.PROPERTIES.getDefaultState();
    private static final BlockState LEAVES = BlockSpruceLeaves.PROPERTIES.getDefaultState();
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        int treeHeight = 7 + random.nextExclusiveInt(5);
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
            if (mayReplace(level.getBlockIdIfCachedOrLoaded(x, trunkY, z))) {
                level.setBlockStateAt(x, trunkY, z, LOG);
                lastTrunkY = trunkY;
            }
        }

        if (lastTrunkY == Integer.MIN_VALUE || !placeCanopy(level, random, x, lastTrunkY + 1, z)) {
            return false;
        }

        String base = level.getBlockIdIfCachedOrLoaded(x, y - 1, z);
        if (!BlockID.DIRT.equals(base) && !BlockID.COARSE_DIRT.equals(base)) {
            level.setBlockStateAt(x, y - 1, z, DIRT);
        }
        return true;
    }

    private static boolean placeCanopy(BlockManager level, RandomSourceProvider random, int x, int y, int z) {
        int canopyHeight = 3 + random.nextExclusiveInt(2);
        int targetRadius = 1 + random.nextExclusiveInt(canopyHeight + 1);
        int radius = 0;
        boolean placed = false;

        for (int relativeY = 1; relativeY > -canopyHeight; relativeY--) {
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    if (radius > 0 && Math.abs(offsetX) == radius && Math.abs(offsetZ) == radius) {
                        continue;
                    }

                    int leafX = x + offsetX;
                    int leafY = y + relativeY;
                    int leafZ = z + offsetZ;
                    if (mayReplace(level.getBlockIdIfCachedOrLoaded(leafX, leafY, leafZ))) {
                        level.setBlockStateAt(leafX, leafY, leafZ, LEAVES);
                        placed = true;
                    }
                }
            }

            int nextRadius = radius >= targetRadius ? radius : radius + 1;
            if (relativeY == 2 - canopyHeight && radius > 0) {
                nextRadius = radius - 1;
            }
            radius = nextRadius;
        }
        return placed;
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
                    if (!mayReplace(level.getBlockIdIfCachedOrLoaded(
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
            case BlockID.DIRT, BlockID.GRASS_BLOCK, BlockID.COARSE_DIRT -> true;
            default -> false;
        };
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
}
