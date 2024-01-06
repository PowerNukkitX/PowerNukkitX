package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockRedConcrete();
    }
}