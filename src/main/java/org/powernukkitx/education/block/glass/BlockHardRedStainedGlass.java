package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardRedStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_RED_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardRedStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}