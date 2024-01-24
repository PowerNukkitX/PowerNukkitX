package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement21 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_21");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement21() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement21(BlockState blockstate) {
        super(blockstate);
    }
}