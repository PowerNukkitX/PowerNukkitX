package org.powernukkitx.level.generator.feature.tree;

import com.google.common.base.Preconditions;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBirchLeaves;
import org.powernukkitx.block.BlockBirchLog;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLeaves;
import org.powernukkitx.block.BlockOakLeaves;
import org.powernukkitx.block.BlockOakLog;
import org.powernukkitx.block.BlockSpruceLeaves;
import org.powernukkitx.block.BlockSpruceLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Classic standing-tree placement used by vanilla world generation.
 *
 * @author Curse
 */
final class ClassicTreePlacementFeature implements PlacementFeature {
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    private static final BlockState VINE_WEST = vine(BlockFace.EAST);
    private static final BlockState VINE_EAST = vine(BlockFace.WEST);
    private static final BlockState VINE_NORTH = vine(BlockFace.SOUTH);
    private static final BlockState VINE_SOUTH = vine(BlockFace.NORTH);

    private final BlockState log;
    private final BlockState leaves;
    private final int minHeight;
    private final int maxHeight;
    private final int minHeightModifier;
    private final int maxHeightModifier;
    private final CanopyType canopyType;
    private final boolean vines;

    private ClassicTreePlacementFeature(
            BlockState log,
            BlockState leaves,
            int minHeight,
            int maxHeight,
            int minHeightModifier,
            int maxHeightModifier,
            CanopyType canopyType,
            boolean vines
    ) {
        this.log = Preconditions.checkNotNull(log, "log");
        this.leaves = Preconditions.checkNotNull(leaves, "leaves");
        Preconditions.checkArgument(maxHeight >= minHeight, "maxHeight must be >= minHeight");
        Preconditions.checkArgument(
                maxHeightModifier >= minHeightModifier,
                "maxHeightModifier must be >= minHeightModifier"
        );
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minHeightModifier = minHeightModifier;
        this.maxHeightModifier = maxHeightModifier;
        this.canopyType = Preconditions.checkNotNull(canopyType, "canopyType");
        this.vines = vines;
    }

    static ClassicTreePlacementFeature oak(boolean vines) {
        return new ClassicTreePlacementFeature(
                BlockOakLog.PROPERTIES.getBlockState(
                        CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
                ),
                BlockOakLeaves.PROPERTIES.getDefaultState(),
                4,
                7,
                0,
                0,
                CanopyType.SIMPLE,
                vines
        );
    }

    static ClassicTreePlacementFeature birch() {
        return new ClassicTreePlacementFeature(
                BlockBirchLog.PROPERTIES.getBlockState(
                        CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
                ),
                BlockBirchLeaves.PROPERTIES.getDefaultState(),
                5,
                8,
                0,
                0,
                CanopyType.SIMPLE,
                false
        );
    }

    static ClassicTreePlacementFeature superBirch() {
        return new ClassicTreePlacementFeature(
                BlockBirchLog.PROPERTIES.getBlockState(
                        CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
                ),
                BlockBirchLeaves.PROPERTIES.getDefaultState(),
                5,
                8,
                0,
                7,
                CanopyType.SIMPLE,
                false
        );
    }

    static ClassicTreePlacementFeature spruce(boolean vines) {
        return new ClassicTreePlacementFeature(
                BlockSpruceLog.PROPERTIES.getBlockState(
                        CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y)
                ),
                BlockSpruceLeaves.PROPERTIES.getDefaultState(),
                6,
                10,
                -2,
                1,
                CanopyType.SPRUCE,
                vines
        );
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        BlockManager level = context.getRoot();
        RandomSourceProvider random = context.getRandom();

        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        int baseHeight = sampleRange(random, this.minHeight, this.maxHeight);
        if (!prepare(level, x, y, z, baseHeight)) {
            return false;
        }

        int treeHeight = baseHeight + sampleRange(random, this.minHeightModifier, this.maxHeightModifier);
        int lastTrunkY = Integer.MIN_VALUE;

        for (int offsetY = 0; offsetY < treeHeight; offsetY++) {
            int trunkY = y + offsetY;
            if (!mayReplace(level.getBlockIdIfCachedOrLoaded(x, trunkY, z))) {
                continue;
            }

            level.setBlockStateAt(x, trunkY, z, this.log);
            if (this.vines) {
                decorateTrunkLog(level, x, trunkY, z);
            }
            lastTrunkY = trunkY;
        }

        if (lastTrunkY == Integer.MIN_VALUE) {
            return false;
        }

        int canopyY = lastTrunkY + 1;
        if (this.canopyType == CanopyType.SPRUCE) {
            placeSpruceCanopy(level, random, x, canopyY, z, y);
        } else {
            placeSimpleCanopy(level, random, x, canopyY, z);
        }

        fixBase(level, x, y - 1, z);
        context.setPosition(x, canopyY, z);
        return true;
    }

    private static boolean prepare(
            BlockManager level,
            int x,
            int y,
            int z,
            int baseHeight
    ) {
        if (y <= level.getMinHeight() || y + baseHeight >= level.getMaxHeight()) {
            return false;
        }
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }

        for (int offsetY = 0; offsetY <= baseHeight + 1; offsetY++) {
            int radius;
            if (offsetY == 0) {
                radius = 0;
            } else if (offsetY >= baseHeight - 1) {
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

    private void placeSimpleCanopy(
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

                    placeLeaf(level, x + offsetX, y + offsetY, z + offsetZ);
                }
            }
        }
    }

    private void placeSpruceCanopy(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int canopyY,
            int z,
            int originY
    ) {
        int lowerOffset = 1 + random.nextExclusiveInt(2);
        int upperOffset = random.nextExclusiveInt(3);
        int maxRadius = 2 + random.nextExclusiveInt(2);

        int radius = random.nextExclusiveInt(2);
        int radiusCeiling = 1;
        int radiusReset = 0;

        int topY = canopyY + upperOffset;
        int bottomY = originY + lowerOffset;

        for (int leafY = topY; leafY >= bottomY; leafY--) {
            placeSpruceLayer(level, x, leafY, z, radius);

            if (radius >= radiusCeiling) {
                radius = radiusReset;
                radiusReset = 1;
                radiusCeiling = Math.min(radiusCeiling + 1, maxRadius);
            } else {
                radius++;
            }
        }
    }

    private void placeSpruceLayer(BlockManager level, int x, int y, int z, int radius) {
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                if (radius > 0
                        && Math.abs(offsetX) == radius
                        && Math.abs(offsetZ) == radius) {
                    continue;
                }

                placeLeaf(level, x + offsetX, y, z + offsetZ);
            }
        }
    }

    private void placeLeaf(BlockManager level, int x, int y, int z) {
        Block block = level.getBlockIfCachedOrLoaded(x, y, z);
        if (isCanopyReplaceable(block)) {
            level.setBlockStateAt(x, y, z, this.leaves);
        }
    }

    private static void decorateTrunkLog(BlockManager level, int x, int y, int z) {
        placeVine(level, x - 1, y, z, VINE_WEST);
        placeVine(level, x + 1, y, z, VINE_EAST);
        placeVine(level, x, y, z - 1, VINE_NORTH);
        placeVine(level, x, y, z + 1, VINE_SOUTH);
    }

    private static void placeVine(BlockManager level, int x, int y, int z, BlockState vine) {
        if (BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private static void fixBase(BlockManager level, int x, int y, int z) {
        String id = level.getBlockIdIfCachedOrLoaded(x, y, z);
        if (!BlockID.DIRT.equals(id) && !BlockID.COARSE_DIRT.equals(id)) {
            level.setBlockStateAt(x, y, z, DIRT);
        }
    }

    private static int sampleRange(RandomSourceProvider random, int min, int max) {
        if (min >= max - 1) {
            return min;
        }
        return min + random.nextExclusiveInt(max - min);
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

    private enum CanopyType {
        SIMPLE,
        SPRUCE
    }
}
