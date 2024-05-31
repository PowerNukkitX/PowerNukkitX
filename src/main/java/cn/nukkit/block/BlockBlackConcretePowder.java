package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(BLACK_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockBlackConcrete();
    }
}