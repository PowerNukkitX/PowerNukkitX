package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockBlackConcrete();
    }
}