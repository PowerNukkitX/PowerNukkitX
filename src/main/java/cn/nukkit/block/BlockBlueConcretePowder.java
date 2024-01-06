package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockBlueConcrete();
    }
}