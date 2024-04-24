package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAzureBluet extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(AZURE_BLUET);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockAzureBluet() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockAzureBluet(BlockState blockstate) {
         super(blockstate);
     }
}