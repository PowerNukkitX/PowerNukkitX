package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGoldenDandelion extends BlockFlower {
     public static final BlockProperties PROPERTIES = new BlockProperties(GOLDEN_DANDELION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockGoldenDandelion() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockGoldenDandelion(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public boolean canBeActivated() {
        return false;
    }
}