package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement100 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_100");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement100() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement100(BlockState blockstate) {
        super(blockstate);
    }
}