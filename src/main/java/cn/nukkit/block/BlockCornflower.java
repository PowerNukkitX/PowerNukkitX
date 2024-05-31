package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCornflower extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(CORNFLOWER);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockCornflower() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockCornflower(BlockState blockstate) {
         super(blockstate);
     }
}