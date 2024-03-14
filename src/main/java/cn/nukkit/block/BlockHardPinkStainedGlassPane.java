package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardPinkStainedGlassPane extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_PINK_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardPinkStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}