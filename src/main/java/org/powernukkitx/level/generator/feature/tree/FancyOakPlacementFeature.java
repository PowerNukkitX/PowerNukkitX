package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockOakLeaves;
import org.powernukkitx.block.BlockOakLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.MathHelper;
import org.powernukkitx.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Fancy-oak placement used by vanilla world generation.
 *
 * @author Curse
 */
final class FancyOakPlacementFeature implements PlacementFeature {
    private static final int BASE_HEIGHT = 5;
    private static final int HEIGHT_VARIANCE = 12;
    private static final int FOLIAGE_HEIGHT = 4;

    private static final float HEIGHT_SCALE = 0.618f;
    private static final float BRANCH_SLOPE = 0.381f;
    private static final float BRANCH_DENSITY = 1.0f;
    private static final float BRANCH_ALTITUDE_FACTOR = 0.2f;
    private static final float FOLIAGE_ALTITUDE_FACTOR = 0.3f;
    private static final float WIDTH_SCALE = 1.0f;
    private static final float BRANCH_LENGTH_OFFSET = 0.328f;

    private static final BlockState LOG_Y = BlockOakLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS,
            BlockFace.Axis.Y
    );
    private static final BlockState LOG_X = BlockOakLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS,
            BlockFace.Axis.X
    );
    private static final BlockState LOG_Z = BlockOakLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS,
            BlockFace.Axis.Z
    );
    private static final BlockState LEAVES = BlockOakLeaves.PROPERTIES.getDefaultState();

    @Override
    public boolean place(FeaturePlacementContext context) {
        BlockManager level = context.getRoot();
        RandomSourceProvider random = context.getRandom();

        int originX = context.getX();
        int originY = context.getY();
        int originZ = context.getZ();

        int sampledHeight = BASE_HEIGHT + random.nextExclusiveInt(HEIGHT_VARIANCE);
        int height = validateHeight(level, originX, originY, originZ, sampledHeight);
        if (height < 0) {
            return false;
        }

        int trunkTop = Math.min(height - 1, (int) (height * HEIGHT_SCALE));
        int clustersPerY = clustersPerY(height);
        int relativeY = height - HEIGHT_VARIANCE / 3;

        List<FoliageNode> foliage = new ArrayList<>();
        foliage.add(new FoliageNode(
                originX,
                originY + relativeY,
                originZ,
                originY + trunkTop
        ));

        for (; relativeY >= 0; relativeY--) {
            float shape = treeShape(height, relativeY);
            if (shape < 0.0f) {
                continue;
            }

            for (int index = 0; index < clustersPerY; index++) {
                float randomDistance = random.nextFloat();
                float randomAngle = random.nextFloat();

                float radialScale = WIDTH_SCALE * shape;
                float distance = (randomDistance + BRANCH_LENGTH_OFFSET) * radialScale;
                float angle = (randomAngle + randomAngle) * (float) Math.PI;

                int candidateX = originX + (int) (MathHelper.sin(angle) * distance + 0.5f);
                int candidateZ = originZ + (int) (MathHelper.cos(angle) * distance + 0.5f);
                int candidateY = originY + relativeY - 1;

                if (checkLine(
                        level,
                        candidateX,
                        candidateY,
                        candidateZ,
                        candidateX,
                        candidateY + HEIGHT_VARIANCE / 3,
                        candidateZ
                ) != -1) {
                    continue;
                }

                int deltaX = originX - candidateX;
                int deltaZ = originZ - candidateZ;
                int horizontalDistance = (int) Math.sqrt(
                        (double) deltaX * deltaX + (double) deltaZ * deltaZ
                );

                float computedBranchBase = candidateY - horizontalDistance * BRANCH_SLOPE;
                int branchBaseY = Math.min(
                        originY + trunkTop,
                        (int) computedBranchBase
                );

                if (checkLine(
                        level,
                        originX,
                        branchBaseY,
                        originZ,
                        candidateX,
                        candidateY,
                        candidateZ
                ) != -1) {
                    continue;
                }

                foliage.add(new FoliageNode(
                        candidateX,
                        candidateY,
                        candidateZ,
                        branchBaseY
                ));
            }
        }

        for (FoliageNode node : foliage) {
            placeCanopy(level, node.x(), node.y(), node.z());
        }

        placeLimb(
                level,
                originX,
                originY,
                originZ,
                originX,
                originY + trunkTop,
                originZ
        );

        for (FoliageNode node : foliage) {
            int localBranchY = node.branchBaseY() - originY;
            if (localBranchY < height * BRANCH_ALTITUDE_FACTOR) {
                continue;
            }

            placeLimb(
                    level,
                    originX,
                    node.branchBaseY(),
                    originZ,
                    node.x(),
                    node.y(),
                    node.z()
            );
        }

        return true;
    }

    private static int validateHeight(
            BlockManager level,
            int x,
            int y,
            int z,
            int sampledHeight
    ) {
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return -1;
        }

        int obstruction = checkLine(
                level,
                x,
                y,
                z,
                x,
                y + sampledHeight - 1,
                z
        );

        if (obstruction == -1) {
            return sampledHeight;
        }

        return obstruction <= BASE_HEIGHT ? -1 : obstruction;
    }

    private static int clustersPerY(int height) {
        float value = BRANCH_DENSITY * height;
        value /= HEIGHT_VARIANCE + 1;
        value *= value;
        value += 1.382f;
        return Math.max(1, (int) value);
    }

    private static float treeShape(int height, int relativeY) {
        if (relativeY < height * FOLIAGE_ALTITUDE_FACTOR) {
            return -1.0f;
        }

        float half = height * 0.5f;
        float adjacent = half - relativeY;

        if (adjacent == 0.0f) {
            return half * 0.5f;
        }
        if (Math.abs(adjacent) >= half) {
            return 0.0f;
        }

        return MathHelper.sqrt(half * half - adjacent * adjacent) * 0.5f;
    }

    private static int checkLine(
            BlockManager level,
            int startX,
            int startY,
            int startZ,
            int endX,
            int endY,
            int endZ
    ) {
        int deltaX = endX - startX;
        int deltaY = endY - startY;
        int deltaZ = endZ - startZ;

        int steps = Math.max(
                Math.abs(deltaX),
                Math.max(Math.abs(deltaY), Math.abs(deltaZ))
        );

        if (steps == 0) {
            return mayGrowThrough(level, startX, startY, startZ) ? -1 : 0;
        }

        float stepX = (float) deltaX / steps;
        float stepY = (float) deltaY / steps;
        float stepZ = (float) deltaZ / steps;

        for (int step = 0; step <= steps; step++) {
            int x = startX + (int) (0.5f + step * stepX);
            int y = startY + (int) (0.5f + step * stepY);
            int z = startZ + (int) (0.5f + step * stepZ);

            if (!mayGrowThrough(level, x, y, z)) {
                return step;
            }
        }

        return -1;
    }

    private static boolean mayGrowThrough(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return false;
        }

        String id = level.getBlockIdIfCachedOrLoaded(x, y, z);
        return switch (id) {
            case BlockID.DIRT,
                 BlockID.GRASS_BLOCK,
                 BlockID.COARSE_DIRT,
                 BlockID.AIR,
                 BlockID.OAK_LEAVES,
                 BlockID.SPRUCE_LEAVES,
                 BlockID.BIRCH_LEAVES,
                 BlockID.JUNGLE_LEAVES,
                 BlockID.ACACIA_LEAVES,
                 BlockID.DARK_OAK_LEAVES -> true;
            default -> false;
        };
    }

    private static boolean mayGrowOn(String id) {
        return switch (id) {
            case BlockID.DIRT,
                 BlockID.GRASS_BLOCK,
                 BlockID.COARSE_DIRT,
                 BlockID.FARMLAND -> true;
            default -> false;
        };
    }

    private static void placeCanopy(BlockManager level, int x, int y, int z) {
        for (int layer = 0; layer < FOLIAGE_HEIGHT; layer++) {
            int radius = layer == 0 || layer == FOLIAGE_HEIGHT - 1 ? 2 : 3;
            placeCanopyLayer(level, x, y + layer, z, radius);
        }
    }

    private static void placeCanopyLayer(
            BlockManager level,
            int x,
            int y,
            int z,
            int radius
    ) {
        float radiusSquared = radius * radius;

        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            float distanceX = Math.abs(offsetX) + 0.5f;

            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                float distanceZ = Math.abs(offsetZ) + 0.5f;

                if (distanceX * distanceX + distanceZ * distanceZ > radiusSquared) {
                    continue;
                }

                int leafX = x + offsetX;
                int leafZ = z + offsetZ;

                if (mayReplace(level, leafX, y, leafZ)) {
                    level.setBlockStateAt(leafX, y, leafZ, LEAVES);
                }
            }
        }
    }

    private static boolean mayReplace(BlockManager level, int x, int y, int z) {
        if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
            return false;
        }

        return switch (level.getBlockIdIfCachedOrLoaded(x, y, z)) {
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

    private static void placeLimb(
            BlockManager level,
            int startX,
            int startY,
            int startZ,
            int endX,
            int endY,
            int endZ
    ) {
        int deltaX = endX - startX;
        int deltaY = endY - startY;
        int deltaZ = endZ - startZ;

        int steps = Math.max(
                Math.abs(deltaX),
                Math.max(Math.abs(deltaY), Math.abs(deltaZ))
        );

        if (steps == 0) {
            level.setBlockStateAt(startX, startY, startZ, LOG_Y);
            return;
        }

        float stepX = (float) deltaX / steps;
        float stepY = (float) deltaY / steps;
        float stepZ = (float) deltaZ / steps;

        for (int step = 0; step <= steps; step++) {
            int offsetX = (int) (0.5f + step * stepX);
            int offsetY = (int) (0.5f + step * stepY);
            int offsetZ = (int) (0.5f + step * stepZ);

            level.setBlockStateAt(
                    startX + offsetX,
                    startY + offsetY,
                    startZ + offsetZ,
                    logState(offsetX, offsetZ)
            );
        }
    }

    private static BlockState logState(int offsetX, int offsetZ) {
        int absX = Math.abs(offsetX);
        int absZ = Math.abs(offsetZ);

        if ((absX | absZ) == 0) {
            return LOG_Y;
        }

        return absX >= absZ ? LOG_X : LOG_Z;
    }

    private record FoliageNode(int x, int y, int z, int branchBaseY) {
    }
}
