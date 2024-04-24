package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends ObjectGenerator {

    /*
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(String id) {

        return switch (id) {
            case Block.AIR, BlockID.ACACIA_LEAVES,
                 BlockID.AZALEA_LEAVES,
                 BlockID.BIRCH_LEAVES,
                 BlockID.AZALEA_LEAVES_FLOWERED,
                 BlockID.CHERRY_LEAVES,
                 BlockID.DARK_OAK_LEAVES,
                 BlockID.JUNGLE_LEAVES,
                 BlockID.MANGROVE_LEAVES,
                 BlockID.OAK_LEAVES,
                 BlockID.SPRUCE_LEAVES, Block.GRASS_BLOCK, Block.DIRT, Block.ACACIA_LOG, Block.BIRCH_LOG, Block.OAK_LOG,
                 Block.DARK_OAK_LOG,
                 Block.JUNGLE_LOG, Block.MANGROVE_LOG, Block.SPRUCE_LOG, Block.VINE,
                 Block.DIRT_WITH_ROOTS,
                 Block.CHERRY_LOG, Block.MANGROVE_ROOTS, Block.MANGROVE_PROPAGULE,
                 BlockID.ACACIA_SAPLING,
                 BlockID.CHERRY_SAPLING,
                 BlockID.SPRUCE_SAPLING,
                 BlockID.BAMBOO_SAPLING,
                 BlockID.OAK_SAPLING,
                 BlockID.JUNGLE_SAPLING,
                 BlockID.DARK_OAK_SAPLING,
                 BlockID.BIRCH_SAPLING -> true;
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

