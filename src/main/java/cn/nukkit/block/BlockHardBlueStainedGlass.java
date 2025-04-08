package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardBlueStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_BLUE_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardBlueStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}