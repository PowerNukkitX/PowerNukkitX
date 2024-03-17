package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardYellowStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_YELLOW_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardYellowStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}