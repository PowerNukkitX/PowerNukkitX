package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLightGrayConcrete();
    }
}