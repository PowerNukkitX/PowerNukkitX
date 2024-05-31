package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardYellowStainedGlass extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_YELLOW_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardYellowStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}