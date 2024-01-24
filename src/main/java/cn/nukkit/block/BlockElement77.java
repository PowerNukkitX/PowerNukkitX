package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement77 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_77");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement77() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement77(BlockState blockstate) {
        super(blockstate);
    }
}