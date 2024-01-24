package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement28 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_28");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement28() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement28(BlockState blockstate) {
        super(blockstate);
    }
}