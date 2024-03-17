package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardLimeStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_LIME_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardLimeStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}