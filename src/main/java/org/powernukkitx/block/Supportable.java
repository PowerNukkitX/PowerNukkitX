package org.powernukkitx.block;

import org.powernukkitx.tags.BlockTags;

public interface Supportable {

    default boolean isSupportDirt(Block block) {
        return block.hasTag(BlockTags.DIRT);
    }

    default boolean isSupportGrass(Block block) {
        return block instanceof BlockGrassBlock && !(block instanceof BlockGrassPath);
    }

}
