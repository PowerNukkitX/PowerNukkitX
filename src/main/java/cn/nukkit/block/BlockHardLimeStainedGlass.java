package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardLimeStainedGlass extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_LIME_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardLimeStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}