package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement48 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_48");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement48() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement48(BlockState blockstate) {
        super(blockstate);
    }
}