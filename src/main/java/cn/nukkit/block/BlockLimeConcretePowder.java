package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLimeConcrete();
    }
}