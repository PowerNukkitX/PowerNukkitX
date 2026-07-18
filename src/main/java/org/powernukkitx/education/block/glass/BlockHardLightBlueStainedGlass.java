package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardLightBlueStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_LIGHT_BLUE_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardLightBlueStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}