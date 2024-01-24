package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockYellowConcrete();
    }
}