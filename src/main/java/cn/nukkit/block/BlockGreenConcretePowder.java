package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockGreenConcrete();
    }
}