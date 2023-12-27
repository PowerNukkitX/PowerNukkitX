package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcretePowder extends Block {
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
}