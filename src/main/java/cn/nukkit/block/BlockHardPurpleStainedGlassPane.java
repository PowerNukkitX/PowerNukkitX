package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardPurpleStainedGlassPane extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_PURPLE_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardPurpleStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}