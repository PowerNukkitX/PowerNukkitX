package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueOrchid extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_ORCHID);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockBlueOrchid() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockBlueOrchid(BlockState blockstate) {
         super(blockstate);
     }
}