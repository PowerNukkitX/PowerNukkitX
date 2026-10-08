package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBirchLog;
import org.powernukkitx.block.BlockBrownMushroom;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockJungleLog;
import org.powernukkitx.block.BlockLeaves;
import org.powernukkitx.block.BlockOakLog;
import org.powernukkitx.block.BlockRedMushroom;
import org.powernukkitx.block.BlockSnowLayer;
import org.powernukkitx.block.BlockSpruceLog;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Fallen-tree placement used by vanilla world generation.
 *
 * @author Curse
 */
final class FallenTreePlacementFeature implements PlacementFeature {
    private static final int[] DIRECTION_X = {0, 0, -1, 1};
    private static final int[] DIRECTION_Z = {-1, 1, 0, 0};

    private static final int[] SHADE_X = {
            0,
            0, 1, 0, -1,
            1, -1, 1, -1,
            0, 2, 0, -2
    };
    private static final int[] SHADE_Z = {
            0,
            -1, 0, 1, 0,
            -1, -1, 1, 1,
            -2, 0, 2, 0
    };

    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    private static final BlockState VINE_WEST = vine(BlockFace.EAST);
    private static final BlockState VINE_EAST = vine(BlockFace.WEST);
    private static final BlockState VINE_NORTH = vine(BlockFace.SOUTH);
    private static final BlockState VINE_SOUTH = vine(BlockFace.NORTH);

    private final WoodType woodType;
    private final int minLogLength;
    private final int maxLogLength;
    private final int minLengthModifier;
    private final int maxLengthModifier;
    private final boolean allowFarmland;
    private final boolean stumpVines;

    private FallenTreePlacementFeature(
            WoodType woodType,
            int minLogLength,
            int maxLogLength,
            int minLengthModifier,
            int maxLengthModifier,
            boolean allowFarmland,
            boolean stumpVines
    ) {
        this.woodType = woodType;
        this.minLogLength = minLogLength;
        this.maxLogLength = maxLogLength;
        this.minLengthModifier = minLengthModifier;
        this.maxLengthModifier = maxLengthModifier;
        this.allowFarmland = allowFarmland;
        this.stumpVines = stumpVines;
    }

    static FallenTreePlacementFeature oak() {
        return new FallenTreePlacementFeature(WoodType.OAK, 4, 7, 0, 0, true, true);
    }

    static FallenTreePlacementFeature birch() {
        return new FallenTreePlacementFeature(WoodType.BIRCH, 5, 8, 0, 0, true, false);
    }

    static FallenTreePlacementFeature superBirch() {
        return new FallenTreePlacementFeature(WoodType.BIRCH, 5, 8, 0, 7, true, false);
    }

    static FallenTreePlacementFeature spruce() {
        return new FallenTreePlacementFeature(WoodType.SPRUCE, 6, 10, 0, 0, false, false);
    }

    static FallenTreePlacementFeature jungle() {
        return new FallenTreePlacementFeature(WoodType.JUNGLE, 4, 11, 0, 0, true, true);
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        BlockManager level = context.getRoot();
        RandomSourceProvider random = context.getRandom();

        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        int baseLogLength = sampleRange(random, this.minLogLength, this.maxLogLength);
        if (!prepare(level, x, y, z, baseLogLength)) {
            return false;
        }

        int lengthModifier = sampleRange(random, this.minLengthModifier, this.maxLengthModifier);
        int direction = random.nextExclusiveInt(4);
        int separation = 2 + random.nextExclusiveInt(2);

        int horizontalLength = baseLogLength + lengthModifier - 2;
        int startX = x + DIRECTION_X[direction] * separation;
        int startZ = z + DIRECTION_Z[direction] * separation;
        int startY = findGroundY(level, startX, y + 1, startZ);

        if (canPlaceHorizontalRun(level, startX, startY, startZ, direction, horizontalLength)) {
            placeHorizontalRun(
                    level,
                    random,
                    startX,
                    startY,
                    startZ,
                    direction,
                    horizontalLength
            );
        }

        boolean stumpPlaced = false;
        if (mayReplace(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, logState(BlockFace.Axis.Y));
            stumpPlaced = true;

            if (this.stumpVines) {
                decorateStump(level, random, x, y, z);
            }
        }

        fixBase(level, x, y - 1, z);

        if (!stumpPlaced) {
            return false;
        }

        context.setPosition(x, y + 1, z);
        return true;
    }

    private boolean prepare(
            BlockManager level,
            int x,
            int y,
            int z,
            int baseLogLength
    ) {
        if (y <= level.getMinHeight() || y + baseLogLength >= level.getMaxHeight()) {
            return false;
        }

        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }

        for (int offsetY = 0; offsetY <= baseLogLength + 1; offsetY++) {
            int radius;
            if (offsetY == 0) {
                radius = 0;
            } else if (offsetY >= baseLogLength - 1) {
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

    private static int findGroundY(BlockManager level, int x, int y, int z) {
        while (y > level.getMinHeight()) {
            Block below = level.getBlockIfCachedOrLoaded(x, y - 1, z);
            if (!below.isAir() && !canBeBuiltOver(below)) {
                break;
            }
            y--;
        }

        return y;
    }

    private static boolean canPlaceHorizontalRun(
            BlockManager level,
            int x,
            int y,
            int z,
            int direction,
            int length
    ) {
        int unsupported = 0;

        for (int index = 0; index < length; index++) {
            int blockX = x + DIRECTION_X[direction] * index;
            int blockZ = z + DIRECTION_Z[direction] * index;

            if (y < level.getMinHeight() || y >= level.getMaxHeight()) {
                return false;
            }

            Block target = level.getBlockIfCachedOrLoaded(blockX, y, blockZ);
            if (!target.isAir() && !canBeBuiltOver(target)) {
                return false;
            }

            Block support = level.getBlockIfCachedOrLoaded(blockX, y - 1, blockZ);
            if (isSolidBlocking(support)) {
                unsupported = 0;
            } else if (++unsupported > 2) {
                return false;
            }
        }

        return true;
    }

    private void placeHorizontalRun(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int direction,
            int length
    ) {
        BlockFace.Axis axis = direction <= 1 ? BlockFace.Axis.Z : BlockFace.Axis.X;
        BlockState log = logState(axis);

        for (int index = 0; index < length; index++) {
            int blockX = x + DIRECTION_X[direction] * index;
            int blockZ = z + DIRECTION_Z[direction] * index;

            level.setBlockStateAt(blockX, y, blockZ, log);
            decorateHorizontalLog(level, random, blockX, y, blockZ);
        }
    }

    private static void decorateHorizontalLog(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        if (random.nextExclusiveInt(10) != 0) {
            return;
        }

        boolean brown = random.nextFloat() * 100.0f < 50.0f;
        int mushroomY = y + 1;

        if (canPlaceLogMushroom(level, x, mushroomY, z)) {
            level.setBlockStateAt(
                    x,
                    mushroomY,
                    z,
                    brown
                            ? BlockBrownMushroom.PROPERTIES.getDefaultState()
                            : BlockRedMushroom.PROPERTIES.getDefaultState()
            );
        }

        consumeMushroomTail(random);
    }

    private static boolean canPlaceLogMushroom(
            BlockManager level,
            int x,
            int y,
            int z
    ) {
        if (!BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            return false;
        }

        for (int index = 0; index < SHADE_X.length; index++) {
            if (!hasMushroomShade(
                    level,
                    x + SHADE_X[index],
                    y,
                    z + SHADE_Z[index]
            )) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasMushroomShade(BlockManager level, int x, int y, int z) {
        if (y < level.getLevel().getHeightMap(x, z)) {
            return true;
        }

        int maxY = Math.min(y + 10, level.getMaxHeight() - 1);
        for (int blockY = y + 1; blockY <= maxY; blockY++) {
            if (level.getBlockIfCachedOrLoaded(x, blockY, z).isSolid()) {
                return true;
            }
        }

        return false;
    }

    private static void consumeMushroomTail(RandomSourceProvider random) {
        random.nextInt();
        random.nextInt();
        random.nextInt();
        random.nextInt();
        random.nextInt();
        random.nextInt();
    }

    private static boolean canBeBuiltOver(Block block) {
        if (block instanceof BlockSnowLayer snow) {
            return snow.getSnowHeight() < CommonBlockProperties.HEIGHT.getMax();
        }

        return block.canBeReplaced();
    }

    private static boolean isSolidBlocking(Block block) {
        return block.isSolid()
                && block.isFullBlock()
                && !(block instanceof BlockLeaves);
    }

    private void decorateStump(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z
    ) {
        placeStumpVine(level, random, x - 1, y, z, VINE_WEST);
        placeStumpVine(level, random, x + 1, y, z, VINE_EAST);
        placeStumpVine(level, random, x, y, z - 1, VINE_NORTH);
        placeStumpVine(level, random, x, y, z + 1, VINE_SOUTH);
    }

    private static void placeStumpVine(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            BlockState vine
    ) {
        if (random.nextFloat() * 100.0f >= 75.0f) {
            return;
        }

        if (BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(x, y, z))) {
            level.setBlockStateAt(x, y, z, vine);
        }
    }

    private boolean mayGrowOn(String id) {
        if (BlockID.DIRT.equals(id)
                || BlockID.GRASS_BLOCK.equals(id)
                || BlockID.PODZOL.equals(id)
                || BlockID.COARSE_DIRT.equals(id)) {
            return true;
        }

        return this.allowFarmland && BlockID.FARMLAND.equals(id);
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

    private BlockState logState(BlockFace.Axis axis) {
        return switch (this.woodType) {
            case BIRCH -> BlockBirchLog.PROPERTIES.getBlockState(
                    CommonBlockProperties.PILLAR_AXIS,
                    axis
            );
            case JUNGLE -> BlockJungleLog.PROPERTIES.getBlockState(
                    CommonBlockProperties.PILLAR_AXIS,
                    axis
            );
            case SPRUCE -> BlockSpruceLog.PROPERTIES.getBlockState(
                    CommonBlockProperties.PILLAR_AXIS,
                    axis
            );
            default -> BlockOakLog.PROPERTIES.getBlockState(
                    CommonBlockProperties.PILLAR_AXIS,
                    axis
            );
        };
    }

    private static BlockState vine(BlockFace supportFace) {
        return BlockVine.PROPERTIES.getBlockState(
                CommonBlockProperties.VINE_DIRECTION_BITS,
                BlockVine.getMetaFromFace(supportFace)
        );
    }
}
