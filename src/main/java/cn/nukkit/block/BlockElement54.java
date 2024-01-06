package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement54 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_54");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement54() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement54(BlockState blockstate) {
        super(blockstate);
    }
}