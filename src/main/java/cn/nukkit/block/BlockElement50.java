package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement50 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_50");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement50() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement50(BlockState blockstate) {
        super(blockstate);
    }
}