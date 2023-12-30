package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
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