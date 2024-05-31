package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardBlueStainedGlass extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_BLUE_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardBlueStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}