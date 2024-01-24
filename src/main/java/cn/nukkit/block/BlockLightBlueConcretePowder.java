package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLightBlueConcrete();
    }
}