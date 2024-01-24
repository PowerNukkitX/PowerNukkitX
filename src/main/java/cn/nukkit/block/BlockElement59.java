package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement59 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_59");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement59() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement59(BlockState blockstate) {
        super(blockstate);
    }
}