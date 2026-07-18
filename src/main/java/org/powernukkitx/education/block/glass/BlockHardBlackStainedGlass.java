package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardBlackStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_BLACK_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardBlackStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}