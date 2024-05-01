package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCornflower extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(CORNFLOWER);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockCornflower() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockCornflower(BlockState blockstate) {
         super(blockstate);
     }
}