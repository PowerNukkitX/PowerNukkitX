package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardBlueStainedGlassPane extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_BLUE_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardBlueStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}