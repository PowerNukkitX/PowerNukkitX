package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement43 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_43");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement43() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement43(BlockState blockstate) {
        super(blockstate);
    }
}