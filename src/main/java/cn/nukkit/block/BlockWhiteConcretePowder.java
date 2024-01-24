package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockWhiteConcrete();
    }
}