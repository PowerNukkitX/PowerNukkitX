package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

import java.util.Objects;
import java.util.Random;

public abstract class TreeGenerator extends ObjectGenerator {

    /*
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(String id) {
        return switch (id) {
            case Block.AIR, Block.LEAVES, Block.GRASS, Block.DIRT, Block.ACACIA_LOG, Block.BIRCH_LOG, Block.OAK_LOG, Block.DARK_OAK_LOG,
                    Block.JUNGLE_LOG, Block.MANGROVE_LOG, Block.SPRUCE_LOG, Block.SAPLING, Block.VINE,
                    Block.DIRT_WITH_ROOTS, Block.AZALEA_LEAVES, Block.AZALEA_LEAVES_FLOWERED, Block.CHERRY_LEAVES,
                    Block.CHERRY_LOG, Block.CHERRY_SAPLING, Block.MANGROVE_LEAVES, Block.MANGROVE_ROOTS, Block.MANGROVE_PROPAGULE ->
                    true;
            default -> false;
        };
    }

    public void generateSaplings(BlockManager level, Random random, Vector3 pos) {
    }

    protected void setDirtAt(BlockManager level, BlockVector3 pos) {
        setDirtAt(level, new Vector3(pos.x, pos.y, pos.z));
    }

    /*
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(BlockManager level, Vector3 pos) {
        level.setBlockStateAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), BlockID.DIRT);
    }
}

