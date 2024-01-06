package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement98 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_98");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement98() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement98(BlockState blockstate) {
        super(blockstate);
    }
}