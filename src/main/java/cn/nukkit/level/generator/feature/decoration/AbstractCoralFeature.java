package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockBrainCoralBlock;
import cn.nukkit.block.BlockBrainCoralFan;
import cn.nukkit.block.BlockBrainCoralWallFan;
import cn.nukkit.block.BlockBubbleCoralBlock;
import cn.nukkit.block.BlockBubbleCoralFan;
import cn.nukkit.block.BlockBubbleCoralWallFan;
import cn.nukkit.block.BlockFireCoralBlock;
import cn.nukkit.block.BlockFireCoralFan;
import cn.nukkit.block.BlockFireCoralWallFan;
import cn.nukkit.block.BlockHornCoralBlock;
import cn.nukkit.block.BlockHornCoralFan;
import cn.nukkit.block.BlockHornCoralWallFan;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSeaPickle;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockTubeCoralBlock;
import cn.nukkit.block.BlockTubeCoralFan;
import cn.nukkit.block.BlockTubeCoralWallFan;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.block.property.CommonBlockProperties.CLUSTER_COUNT;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_DIRECTION;
import static cn.nukkit.level.generator.feature.river.DiscGenerateFeature.STATE_STILL_WATER;

public abstract class AbstractCoralFeature extends WaterFoliageFeature {
    private static final BlockState[] CORAL_BLOCKS = new BlockState[]{
            BlockTubeCoralBlock.PROPERTIES.getDefaultState(),
            BlockBrainCoralBlock.PROPERTIES.getDefaultState(),
            BlockBubbleCoralBlock.PROPERTIES.getDefaultState(),
            BlockFireCoralBlock.PROPERTIES.getDefaultState(),
            BlockHornCoralBlock.PROPERTIES.getDefaultState()
    };

    private static final BlockState[] CORAL_FANS = new BlockState[]{
            BlockTubeCoralFan.PROPERTIES.getDefaultState(),
            BlockBrainCoralFan.PROPERTIES.getDefaultState(),
            BlockBubbleCoralFan.PROPERTIES.getDefaultState(),
            BlockFireCoralFan.PROPERTIES.getDefaultState(),
            BlockHornCoralFan.PROPERTIES.getDefaultState()
    };

    private static final BlockState[] CORAL_WALL_FANS = new BlockState[]{
            BlockTubeCoralWallFan.PROPERTIES.getDefaultState(),
            BlockBrainCoralWallFan.PROPERTIES.getDefaultState(),
            BlockBubbleCoralWallFan.PROPERTIES.getDefaultState(),
            BlockFireCoralWallFan.PROPERTIES.getDefaultState(),
            BlockHornCoralWallFan.PROPERTIES.getDefaultState()
    };

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk) {
        return inBounds(x, y, z, chunk)
                && inBounds(x, y - 1, z, chunk)
                && isWater(chunk.getBlockState(x, y, z))
                && isSand(chunk.getBlockState(x, y - 1, z));
    }

    @Override
    protected void placeBlock(int x, int y, int z, IChunk chunk, RandomSourceProvider random) {
        BlockState coralState = CORAL_BLOCKS[random.nextInt(CORAL_BLOCKS.length)];
        placeFeature(chunk, random, x, y, z, coralState);
    }

    protected abstract boolean placeFeature(IChunk chunk, RandomSourceProvider random, int x, int y, int z, BlockState coralState);

    protected boolean placeCoralBlock(IChunk chunk, RandomSourceProvider random, int x, int y, int z, BlockState coralState) {
        if (!inBounds(x, y, z, chunk) || !inBounds(x, y + 1, z, chunk)) {
            return false;
        }

        BlockState target = chunk.getBlockState(x, y, z);
        if ((!isWater(target) && !isCoral(target)) || !isWater(chunk.getBlockState(x, y + 1, z))) {
            return false;
        }

        chunk.setBlockState(x, y, z, coralState);
        chunk.setBlockState(x, y, z, STATE_STILL_WATER, 1);

        if (random.nextFloat() < 0.25f) {
            BlockState topCoral = CORAL_FANS[random.nextInt(CORAL_FANS.length)];
            chunk.setBlockState(x, y + 1, z, topCoral);
            chunk.setBlockState(x, y + 1, z, STATE_STILL_WATER, 1);
        } else if (random.nextFloat() < 0.05f) {
            BlockState seaPickle = BlockSeaPickle.PROPERTIES.getBlockState(CLUSTER_COUNT.createValue(random.nextInt(4)));
            chunk.setBlockState(x, y + 1, z, seaPickle);
            chunk.setBlockState(x, y + 1, z, STATE_STILL_WATER, 1);
        }

        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (random.nextFloat() >= 0.2f) {
                continue;
            }
            int nx = x + face.getXOffset();
            int nz = z + face.getZOffset();
            if (!inBounds(nx, y, nz, chunk) || !isWater(chunk.getBlockState(nx, y, nz))) {
                continue;
            }
            BlockState wallCoral = CORAL_WALL_FANS[random.nextInt(CORAL_WALL_FANS.length)]
                    .setPropertyValue(BlockTubeCoralWallFan.PROPERTIES, CORAL_DIRECTION, coralDirection(face));
            chunk.setBlockState(nx, y, nz, wallCoral);
            chunk.setBlockState(nx, y, nz, STATE_STILL_WATER, 1);
        }

        return true;
    }

    private static int coralDirection(BlockFace face) {
        return switch (face) {
            case WEST -> 0;
            case EAST -> 1;
            case NORTH -> 2;
            case SOUTH -> 3;
            default -> 2;
        };
    }

    protected static boolean inBounds(int x, int y, int z, IChunk chunk) {
        return x >= 0 && x <= 15 && z >= 0 && z <= 15 && y >= chunk.getLevel().getMinHeight() && y < chunk.getLevel().getMaxHeight();
    }

    protected static boolean isWater(BlockState state) {
        String id = state.getIdentifier();
        return BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id);
    }

    private static boolean isCoral(BlockState state) {
        return state.getIdentifier().contains("coral");
    }

    private static boolean isSand(BlockState state) {
        String id = state.getIdentifier();
        return BlockID.SAND.equals(id) || BlockID.RED_SAND.equals(id);
    }
}
