package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement113 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_113");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement113() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement113(BlockState blockstate) {
        super(blockstate);
    }
}