package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement8 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_8");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement8() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement8(BlockState blockstate) {
        super(blockstate);
    }
}