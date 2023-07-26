package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ObjectCrimsonTree extends ObjectNetherTree {
    @Override
    public int getTrunkBlock() {
        return Block.CRIMSON_STEM;
    }

    @Override
    public int getLeafBlock() {
        return Block.BLOCK_NETHER_WART_BLOCK;
    }
}
