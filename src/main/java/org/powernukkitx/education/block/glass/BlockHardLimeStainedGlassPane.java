package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardLimeStainedGlassPane extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_LIME_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardLimeStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}