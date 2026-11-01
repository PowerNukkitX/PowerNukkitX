package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLeaves;
import org.powernukkitx.block.BlockOrangePoplarLeaves;
import org.powernukkitx.block.BlockPoplarLog;
import org.powernukkitx.block.BlockRedPoplarLeaves;
import org.powernukkitx.block.BlockShelfMushroom;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockYellowPoplarLeaves;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Standing poplar tree placement.
 *
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/10/31
 */
public final class ObjectPoplarTree extends TreeGenerator {
    private static final int MIN_HEIGHT = 7;
    private static final int HEIGHT_VARIANCE = 4;
    private static final int TRUNK_ABOVE_BRANCHES = 4;
    private static final float EDGE_GAP_CHANCE = 0.15f;
    private static final float SHELF_MUSHROOM_CHANCE = 0.25f;

    private static final BlockState VERTICAL_LOG = log(BlockFace.Axis.Y);
    private static final BlockState EAST_WEST_LOG = log(BlockFace.Axis.X);
    private static final BlockState NORTH_SOUTH_LOG = log(BlockFace.Axis.Z);
    private static final BlockState DIRT = BlockDirt.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider random, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();
        int trunkHeight = MIN_HEIGHT + random.nextExclusiveInt(HEIGHT_VARIANCE + 1);

        if (!prepare(level, x, y, z, trunkHeight)) {
            return false;
        }

        BlockState leaves = selectLeaves(random);
        int branchY = y + trunkHeight - TRUNK_ABOVE_BRANCHES - 1;

        fixBase(level, x, y - 1, z);
        placeTrunk(level, random, x, y, z, trunkHeight, branchY);
        placeShelfMushrooms(level, random, x, y, z, branchY);
        placeCanopy(level, random, leaves, x, branchY + 1, z);

        return true;
    }

    private static boolean prepare(BlockManager level, int x, int y, int z, int trunkHeight) {
        if (y <= level.getMinHeight() || y + trunkHeight + 1 >= level.getMaxHeight()) {
            return false;
        }
        if (!mayGrowOn(level.getBlockIdIfCachedOrLoaded(x, y - 1, z))) {
            return false;
        }

        for (int offsetY = 0; offsetY < trunkHeight; offsetY++) {
            if (!mayReplace(level.getBlockIfCachedOrLoaded(x, y + offsetY, z))) {
                return false;
            }
        }
        return true;
    }

    private static void placeTrunk(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int trunkHeight,
            int branchY
    ) {
        for (int offsetY = 0; offsetY < trunkHeight; offsetY++) {
            level.setBlockStateAt(x, y + offsetY, z, VERTICAL_LOG);
        }

        BlockFace[] directions = BlockFace.getHorizontals();
        for (int remaining = directions.length; remaining > 1; remaining--) {
            int selected = random.nextExclusiveInt(remaining);
            BlockFace direction = directions[selected];
            directions[selected] = directions[remaining - 1];
            directions[remaining - 1] = direction;
        }

        int branches = 1 + random.nextExclusiveInt(4);
        for (int index = 0; index < branches; index++) {
            BlockFace direction = directions[index];
            int branchX = x + direction.getXOffset();
            int branchZ = z + direction.getZOffset();
            if (mayReplace(level.getBlockIfCachedOrLoaded(branchX, branchY, branchZ))) {
                BlockState branch = direction.getAxis() == BlockFace.Axis.X
                        ? EAST_WEST_LOG
                        : NORTH_SOUTH_LOG;
                level.setBlockStateAt(branchX, branchY, branchZ, branch);
            }
        }
    }

    private static void placeCanopy(
            BlockManager level,
            RandomSourceProvider random,
            BlockState leaves,
            int x,
            int y,
            int z
    ) {
        int foliageHeight = 5 + random.nextExclusiveInt(2);
        int radius = sampleRadius(random) - 1;
        boolean alternateCorners = random.nextBoolean();

        placeLeafLayer(level, random, leaves, x, y, z, radius - 2, foliageHeight - 1, foliageHeight, alternateCorners);
        placeLeafLayer(level, random, leaves, x, y, z, radius - 1, foliageHeight - 2, foliageHeight, alternateCorners);
        placeLeafLayer(level, random, leaves, x, y, z, radius - 1, foliageHeight - 3, foliageHeight, alternateCorners);

        for (int offsetY = foliageHeight - 4; offsetY >= 1; offsetY--) {
            placeLeafLayer(level, random, leaves, x, y, z, radius, offsetY, foliageHeight, alternateCorners);
        }

        placeCrossBranches(level, leaves, x, y, z, radius, foliageHeight - 4, foliageHeight, alternateCorners);
        placeLeafLayer(level, random, leaves, x, y, z, radius - 1, 0, foliageHeight, alternateCorners);
        placeLeafLayer(level, random, leaves, x, y, z, Math.max(1, radius - 2), -1, foliageHeight, alternateCorners);
    }

    private static void placeShelfMushrooms(
            BlockManager level,
            RandomSourceProvider random,
            int x,
            int y,
            int z,
            int branchY
    ) {
        if (random.nextFloat() >= SHELF_MUSHROOM_CHANCE) {
            return;
        }

        BlockFace[] directions = BlockFace.getHorizontals();
        int attempts = 1 + random.nextExclusiveInt(2);
        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockFace facing = directions[random.nextExclusiveInt(directions.length)];
            int mushroomY = y + 1 + random.nextExclusiveInt(Math.max(1, branchY - y - 1));
            int mushroomX = x + facing.getXOffset();
            int mushroomZ = z + facing.getZOffset();
            if (!BlockID.AIR.equals(level.getBlockIdIfCachedOrLoaded(mushroomX, mushroomY, mushroomZ))) {
                continue;
            }

            BlockShelfMushroom mushroom = new BlockShelfMushroom();
            mushroom.setFacing(facing);
            mushroom.setPropertyValue(CommonBlockProperties.GROWTH_1, random.nextExclusiveInt(2));
            level.setBlockStateAt(mushroomX, mushroomY, mushroomZ, mushroom.getBlockState());
        }
    }

    private static void placeLeafLayer(
            BlockManager level,
            RandomSourceProvider random,
            BlockState leaves,
            int x,
            int y,
            int z,
            int radius,
            int offsetY,
            int foliageHeight,
            boolean alternateCorners
    ) {
        boolean partialLayer = isPartialLayer(foliageHeight, offsetY);
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                if (shouldSkipLeaf(random, offsetX, offsetZ, radius, partialLayer, alternateCorners)) {
                    continue;
                }

                int leafX = x + offsetX;
                int leafY = y + offsetY;
                int leafZ = z + offsetZ;
                if (mayReplace(level.getBlockIfCachedOrLoaded(leafX, leafY, leafZ))) {
                    level.setBlockStateAt(leafX, leafY, leafZ, leaves);
                }
            }
        }
    }

    private static boolean shouldSkipLeaf(
            RandomSourceProvider random,
            int offsetX,
            int offsetZ,
            int radius,
            boolean partialLayer,
            boolean alternateCorners
    ) {
        int absoluteX = Math.abs(offsetX);
        int absoluteZ = Math.abs(offsetZ);
        if (partialLayer && (absoluteX == radius || absoluteZ == radius)) {
            return true;
        }

        int cornerCut = cornerCut(offsetX, offsetZ, radius, partialLayer, alternateCorners);
        int edgeGap = random.nextFloat() <= EDGE_GAP_CHANCE ? 1 : 0;
        return absoluteX + absoluteZ > radius * 2 - cornerCut - edgeGap;
    }

    private static void placeCrossBranches(
            BlockManager level,
            BlockState leaves,
            int x,
            int y,
            int z,
            int radius,
            int offsetY,
            int foliageHeight,
            boolean alternateCorners
    ) {
        boolean partialLayer = isPartialLayer(foliageHeight, offsetY);
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                int absoluteX = Math.abs(offsetX);
                int absoluteZ = Math.abs(offsetZ);
                int cornerCut = cornerCut(offsetX, offsetZ, radius, partialLayer, alternateCorners);
                boolean insideCanopy = absoluteX + absoluteZ <= radius * 2 - cornerCut - 2;
                boolean insideCross = absoluteZ == 0 && radius - absoluteX >= 4
                        || absoluteX == 0 && radius - absoluteZ >= 4;
                if (!insideCanopy || !insideCross) {
                    continue;
                }

                int logX = x + offsetX;
                int logY = y + offsetY;
                int logZ = z + offsetZ;
                if (!leaves.getIdentifier().equals(level.getBlockIdIfCachedOrLoaded(logX, logY, logZ))) {
                    continue;
                }

                BlockState log = absoluteZ == 0 ? EAST_WEST_LOG : NORTH_SOUTH_LOG;
                level.setBlockStateAt(logX, logY, logZ, log);
            }
        }
    }

    private static int cornerCut(
            int offsetX,
            int offsetZ,
            int radius,
            boolean partialLayer,
            boolean alternateCorners
    ) {
        boolean shortCorner = alternateCorners
                ? offsetX > 0 && offsetZ > 0 || offsetX < 0 && offsetZ < 0
                : offsetX > 0 && offsetZ < 0 || offsetX < 0 && offsetZ > 0;
        if (shortCorner) {
            return radius - 1;
        }
        return partialLayer ? radius + 1 : radius;
    }

    private static boolean isPartialLayer(int foliageHeight, int offsetY) {
        return offsetY == foliageHeight - 1 || offsetY == foliageHeight - 2;
    }

    private static int sampleRadius(RandomSourceProvider random) {
        int sample = random.nextExclusiveInt(12);
        if (sample < 5) {
            return 5;
        }
        if (sample < 10) {
            return 6;
        }
        return sample == 10 ? 7 : 8;
    }

    private static BlockState selectLeaves(RandomSourceProvider random) {
        return switch (random.nextExclusiveInt(3)) {
            case 0 -> BlockRedPoplarLeaves.PROPERTIES.getDefaultState();
            case 1 -> BlockOrangePoplarLeaves.PROPERTIES.getDefaultState();
            default -> BlockYellowPoplarLeaves.PROPERTIES.getDefaultState();
        };
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

    private static boolean mayReplace(Block block) {
        return BlockID.AIR.equals(block.getId())
                || BlockID.POPLAR_SAPLING.equals(block.getId())
                || BlockID.VINE.equals(block.getId())
                || block instanceof BlockLeaves;
    }

    private static void fixBase(BlockManager level, int x, int y, int z) {
        String id = level.getBlockIdIfCachedOrLoaded(x, y, z);
        if (!BlockID.DIRT.equals(id) && !BlockID.COARSE_DIRT.equals(id)) {
            level.setBlockStateAt(x, y, z, DIRT);
        }
    }

    private static BlockState log(BlockFace.Axis axis) {
        return BlockPoplarLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
    }
}
