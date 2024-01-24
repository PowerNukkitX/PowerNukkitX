package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockPinkConcrete();
    }
}