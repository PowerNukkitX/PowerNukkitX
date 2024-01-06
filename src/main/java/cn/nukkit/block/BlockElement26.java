package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement26 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_26");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement26() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement26(BlockState blockstate) {
        super(blockstate);
    }
}