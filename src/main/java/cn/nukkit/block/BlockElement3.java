package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement3 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_3");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement3(BlockState blockstate) {
        super(blockstate);
    }
}