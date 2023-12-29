package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockOrangeConcrete();
    }
}