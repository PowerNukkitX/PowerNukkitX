package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(LIME_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLimeConcrete();
    }
}