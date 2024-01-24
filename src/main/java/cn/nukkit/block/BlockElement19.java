package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement19 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_19");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement19() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement19(BlockState blockstate) {
        super(blockstate);
    }
}