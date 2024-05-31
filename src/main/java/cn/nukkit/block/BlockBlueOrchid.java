package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueOrchid extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(BLUE_ORCHID);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockBlueOrchid() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockBlueOrchid(BlockState blockstate) {
         super(blockstate);
     }
}