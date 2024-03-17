package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardPurpleStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_PURPLE_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardPurpleStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}