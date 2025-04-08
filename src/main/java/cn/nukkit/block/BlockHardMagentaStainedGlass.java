package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardMagentaStainedGlass extends Block {
     public static final BlockProperties PROPERTIES = new BlockProperties(HARD_MAGENTA_STAINED_GLASS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockHardMagentaStainedGlass(BlockState blockstate) {
         super(blockstate);
     }
}