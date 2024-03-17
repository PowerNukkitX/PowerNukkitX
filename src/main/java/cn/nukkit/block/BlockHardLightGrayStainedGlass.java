package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardLightGrayStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_LIGHT_GRAY_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardLightGrayStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}