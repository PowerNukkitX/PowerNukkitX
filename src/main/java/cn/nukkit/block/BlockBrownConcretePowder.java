package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(BROWN_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockBrownConcrete();
    }
}