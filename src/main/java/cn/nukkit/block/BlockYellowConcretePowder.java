package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
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