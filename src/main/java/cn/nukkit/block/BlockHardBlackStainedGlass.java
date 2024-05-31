package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardBlackStainedGlass extends Block {
     public static final BlockProperties $1 = new BlockProperties(HARD_BLACK_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockHardBlackStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}