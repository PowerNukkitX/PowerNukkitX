package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockGrayConcrete();
    }
}