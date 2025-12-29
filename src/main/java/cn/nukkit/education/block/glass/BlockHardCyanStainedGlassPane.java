package cn.nukkit.education.block.glass;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardCyanStainedGlassPane extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_CYAN_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardCyanStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}