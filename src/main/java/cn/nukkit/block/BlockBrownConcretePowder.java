package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockBrownConcrete();
    }
}