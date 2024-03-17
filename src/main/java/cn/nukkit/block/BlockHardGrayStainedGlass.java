package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardGrayStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_GRAY_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardGrayStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}