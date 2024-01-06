package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement63 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_63");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement63() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement63(BlockState blockstate) {
        super(blockstate);
    }
}