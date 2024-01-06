package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement65 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_65");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement65() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement65(BlockState blockstate) {
        super(blockstate);
    }
}