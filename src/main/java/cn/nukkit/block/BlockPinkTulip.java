package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkTulip extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(PINK_TULIP);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockPinkTulip() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockPinkTulip(BlockState blockstate) {
         super(blockstate);
     }
}