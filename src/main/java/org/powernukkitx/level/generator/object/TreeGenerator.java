package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockVine;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends ObjectGenerator {
    protected static final int TREE_WITH_VINES_CHANCE = 20;

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
                 BlockID.SPRUCE_LEAVES,
                 BlockID.PALE_OAK_LEAVES,
                 Block.GRASS_BLOCK, Block.DIRT, Block.ACACIA_LOG, Block.BIRCH_LOG, Block.OAK_LOG, Block.PALE_OAK_LOG,
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
                 BlockID.PALE_OAK_SAPLING,
                 BlockID.BIRCH_SAPLING,
                 BlockID.FERN,
                 BlockID.SHORT_GRASS,
                 BlockID.TALL_GRASS,
                 BlockID.PALE_HANGING_MOSS,
                 BlockID.CLOSED_EYEBLOSSOM,
                 BlockID.OPEN_EYEBLOSSOM,
                 BlockID.LEAF_LITTER,
                 BlockID.BAMBOO-> true;
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

    protected void addVinesAroundLog(BlockManager level, int x, int y, int z) {
        this.addVine(level, x - 1, y, z, 8);
        this.addVine(level, x + 1, y, z, 2);
        this.addVine(level, x, y, z - 1, 1);
        this.addVine(level, x, y, z + 1, 4);
    }

    private void addVine(BlockManager level, int x, int y, int z, int meta) {
        if (level.getBlockIdIfCachedOrLoaded(x, y, z).equals(Block.AIR)) {
            level.setBlockStateAt(x, y, z, getVineState(meta));
        }
    }

    protected static BlockState getVineState(int meta) {
        return BlockVine.PROPERTIES.getBlockState(CommonBlockProperties.VINE_DIRECTION_BITS, meta);
    }
}
