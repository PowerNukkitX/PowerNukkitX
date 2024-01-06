package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockOrangeConcrete();
    }
}