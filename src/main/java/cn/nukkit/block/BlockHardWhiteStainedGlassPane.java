package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardWhiteStainedGlassPane extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_WHITE_STAINED_GLASS_PANE);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardWhiteStainedGlassPane(BlockState blockstate) {
         super(blockstate);
     }
}