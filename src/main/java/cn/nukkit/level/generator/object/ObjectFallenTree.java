package cn.nukkit.level.generator.object;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;

public class ObjectFallenTree extends TreeGenerator {
    private static final int FALLEN_LOG_MAX_GROUND_GAP = 2;
    private static final int MAX_MUSHROOMS = 2;

    private final WoodType woodType;
    private final int minLogLength;
    private final int maxLogLength;

    public ObjectFallenTree() {
        this(WoodType.OAK, 3, 7);
    }

    public ObjectFallenTree(WoodType woodType) {
        this(woodType, 3, 7);
    }

    public ObjectFallenTree(WoodType woodType, int minLogLength, int maxLogLength) {
        this.woodType = woodType;
        this.minLogLength = minLogLength;
        this.maxLogLength = maxLogLength;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        BlockVector3 origin = position.asBlockVector3();
        if (!this.mayPlaceOn(level, origin)) {
            return false;
        }

        this.placeLogBlock(level, origin, BlockFace.Axis.Y);
        this.decorateStump(level, rand, origin);

        BlockFace direction = BlockFace.Plane.HORIZONTAL.random(rand);
        int logLength = this.sampleLogLength(rand);
        BlockVector3 logStartPos = origin.getSide(direction, 2 + rand.nextInt(2));
        this.setGroundHeightForFallenLogStartPos(level, logStartPos);

        if (this.canPlaceEntireFallenLog(level, logLength, logStartPos, direction)) {
            List<BlockVector3> fallenLog = this.placeFallenLog(level, logLength, logStartPos, direction);
            this.decorateFallenLog(level, rand, fallenLog);
        }

        level.addHook(() -> {
            for(Block block : level.getBlocks()) {
                if(block.up() instanceof BlockFlower flower && !flower.canPlantOn(block)) {
                    level.getLevel().setBlock(block.up(), BlockAir.STATE.toBlock());
                }
            }
        });
        return true;
    }

    private int sampleLogLength(RandomSourceProvider random) {
        if (this.maxLogLength <= this.minLogLength) {
            return Math.max(1, this.minLogLength);
        }

        return this.minLogLength + random.nextInt(this.maxLogLength - this.minLogLength + 1);
    }

    private void setGroundHeightForFallenLogStartPos(BlockManager level, BlockVector3 logStartPos) {
        logStartPos.y++;

        for (int i = 0; i < 6; i++) {
            if (this.mayPlaceOn(level, logStartPos)) {
                return;
            }

            logStartPos.y--;
        }
    }

    private boolean canPlaceEntireFallenLog(BlockManager level, int logLength, BlockVector3 logStartPos, BlockFace direction) {
        int gapInGround = 0;
        BlockVector3 current = logStartPos.clone();

        for (int i = 0; i < logLength; i++) {
            if (!this.validFallenLogPos(level, current)) {
                return false;
            }

            if (!this.isOverSolidGround(level, current)) {
                if (++gapInGround > FALLEN_LOG_MAX_GROUND_GAP) {
                    return false;
                }
            } else {
                gapInGround = 0;
            }

            current = current.getSide(direction);
        }

        return true;
    }

    private List<BlockVector3> placeFallenLog(BlockManager level, int logLength, BlockVector3 logStartPos, BlockFace direction) {
        List<BlockVector3> fallenLog = new ArrayList<>(logLength);
        BlockVector3 current = logStartPos.clone();
        for (int i = 0; i < logLength; i++) {
            this.placeLogBlock(level, current, direction.getAxis());
            fallenLog.add(current);
            current = current.getSide(direction);
        }

        return fallenLog;
    }

    private void decorateStump(BlockManager level, RandomSourceProvider random, BlockVector3 stumpPos) {
        if (this.woodType != WoodType.OAK && this.woodType != WoodType.JUNGLE) {
            return;
        }

        this.placeStumpVine(level, random, stumpPos.west(), 8);
        this.placeStumpVine(level, random, stumpPos.east(), 2);
        this.placeStumpVine(level, random, stumpPos.north(), 1);
        this.placeStumpVine(level, random, stumpPos.south(), 4);
    }

    private void placeStumpVine(BlockManager level, RandomSourceProvider random, BlockVector3 pos, int meta) {
        if (random.nextInt(4) == 0 || !level.getBlockIfCachedOrLoaded(pos.x, pos.y, pos.z).isAir()) {
            return;
        }

        level.setBlockStateAt(pos, BlockVine.PROPERTIES.getBlockState(CommonBlockProperties.VINE_DIRECTION_BITS, meta));
    }

    private void decorateFallenLog(BlockManager level, RandomSourceProvider random, List<BlockVector3> fallenLog) {
        if (fallenLog.isEmpty() || random.nextInt(4) != 0) {
            return;
        }

        int mushrooms = 1 + random.nextInt(MAX_MUSHROOMS);
        for (int i = 0; i < mushrooms; i++) {
            BlockVector3 logPos = fallenLog.get(random.nextInt(fallenLog.size()));
            BlockVector3 mushroomPos = logPos.up();
            if (level.getBlockIfCachedOrLoaded(mushroomPos.x, mushroomPos.y, mushroomPos.z).isAir()) {
                level.setBlockStateAt(mushroomPos, this.getMushroomState(random));
            }
        }
    }

    private BlockState getMushroomState(RandomSourceProvider random) {
        return random.nextBoolean()
                ? BlockRedMushroom.PROPERTIES.getDefaultState()
                : BlockBrownMushroom.PROPERTIES.getDefaultState();
    }

    private boolean mayPlaceOn(BlockManager level, BlockVector3 pos) {
        return this.validFallenLogPos(level, pos) && this.isOverSolidGround(level, pos);
    }

    private boolean validFallenLogPos(BlockManager level, BlockVector3 pos) {
        if (pos.y < level.getMinHeight() || pos.y >= level.getMaxHeight()) {
            return false;
        }

        Block block = level.getBlockIfCachedOrLoaded(pos.x, pos.y, pos.z);
        return !block.isSolid();
    }

    private boolean isOverSolidGround(BlockManager level, BlockVector3 pos) {
        return level.getBlockIfCachedOrLoaded(pos.x, pos.y - 1, pos.z).isSolid();
    }

    private void placeLogBlock(BlockManager level, BlockVector3 pos, BlockFace.Axis axis) {
        level.setBlockStateAt(pos, this.getLogState(axis));
    }

    private BlockState getLogState(BlockFace.Axis axis) {
        return switch (this.woodType) {
            case ACACIA -> BlockAcaciaLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case BIRCH -> BlockBirchLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case CHERRY -> BlockCherryLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case DARK_OAK -> BlockDarkOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case JUNGLE -> BlockJungleLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case MANGROVE -> BlockMangroveLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case PALE_OAK -> BlockPaleOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case SPRUCE -> BlockSpruceLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
            case OAK -> BlockOakLog.PROPERTIES.getBlockState(CommonBlockProperties.PILLAR_AXIS, axis);
        };
    }
}
