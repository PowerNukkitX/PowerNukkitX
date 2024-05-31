package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardBrownStainedGlassPane extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_BROWN_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardBrownStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}