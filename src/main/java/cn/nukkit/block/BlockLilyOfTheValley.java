package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLilyOfTheValley extends BlockFlower {
     public static final BlockProperties $1 = new BlockProperties(LILY_OF_THE_VALLEY);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockLilyOfTheValley() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockLilyOfTheValley(BlockState blockstate) {
         super(blockstate);
     }
}