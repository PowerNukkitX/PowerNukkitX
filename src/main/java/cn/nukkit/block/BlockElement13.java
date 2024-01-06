package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement13 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_13");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement13() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement13(BlockState blockstate) {
        super(blockstate);
    }
}