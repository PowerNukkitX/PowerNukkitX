package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement53 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_53");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement53() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement53(BlockState blockstate) {
        super(blockstate);
    }
}