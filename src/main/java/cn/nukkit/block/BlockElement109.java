package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement109 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_109");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement109() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement109(BlockState blockstate) {
        super(blockstate);
    }
}