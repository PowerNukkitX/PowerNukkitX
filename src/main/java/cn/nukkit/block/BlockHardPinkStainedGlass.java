package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardPinkStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_PINK_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardPinkStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}