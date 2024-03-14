package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardRedStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_RED_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardRedStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}