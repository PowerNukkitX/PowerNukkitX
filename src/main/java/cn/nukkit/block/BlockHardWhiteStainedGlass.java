package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardWhiteStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_WHITE_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardWhiteStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}