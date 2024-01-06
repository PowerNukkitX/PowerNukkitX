package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement56 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_56");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement56() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement56(BlockState blockstate) {
        super(blockstate);
    }
}