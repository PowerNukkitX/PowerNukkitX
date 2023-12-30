package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
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