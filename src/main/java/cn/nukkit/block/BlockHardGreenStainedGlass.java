package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardGreenStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_GREEN_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardGreenStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}