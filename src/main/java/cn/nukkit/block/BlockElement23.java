package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement23 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_23");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement23() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement23(BlockState blockstate) {
        super(blockstate);
    }
}