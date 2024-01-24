package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement114 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_114");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement114() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement114(BlockState blockstate) {
        super(blockstate);
    }
}