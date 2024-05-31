package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(ORANGE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockOrangeConcrete();
    }
}