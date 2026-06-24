package cn.nukkit.block;

import cn.nukkit.tags.BlockTags;

public interface Supportable {

    default boolean isSupportDirt(Block block) {
        return block.hasTag(BlockTags.DIRT);
    }

}
