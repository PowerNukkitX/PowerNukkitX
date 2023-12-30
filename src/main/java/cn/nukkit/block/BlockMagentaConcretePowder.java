package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcretePowder extends BlockConcretePowder {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockMagentaConcrete();
    }
}