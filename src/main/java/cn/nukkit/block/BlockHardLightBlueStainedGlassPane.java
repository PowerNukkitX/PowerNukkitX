package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardLightBlueStainedGlassPane extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_LIGHT_BLUE_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardLightBlueStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}