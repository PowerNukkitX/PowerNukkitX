package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardBrownStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_BROWN_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardBrownStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}