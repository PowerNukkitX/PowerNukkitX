package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardGreenStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_GREEN_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardGreenStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}