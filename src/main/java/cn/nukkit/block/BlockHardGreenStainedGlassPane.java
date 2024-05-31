package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardGreenStainedGlassPane extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_GREEN_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardGreenStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}