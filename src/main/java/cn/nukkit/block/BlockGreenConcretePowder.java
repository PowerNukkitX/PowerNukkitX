package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
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