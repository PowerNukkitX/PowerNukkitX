package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAzureBluet extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(AZURE_BLUET);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockAzureBluet() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockAzureBluet(BlockState blockstate) {
         super(blockstate);
     }
}