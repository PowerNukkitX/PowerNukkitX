package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLilyOfTheValley extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(LILY_OF_THE_VALLEY);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockLilyOfTheValley() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockLilyOfTheValley(BlockState blockstate) {
         super(blockstate);
     }
}