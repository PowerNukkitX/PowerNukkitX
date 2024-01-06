package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement58 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_58");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement58() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement58(BlockState blockstate) {
        super(blockstate);
    }
}