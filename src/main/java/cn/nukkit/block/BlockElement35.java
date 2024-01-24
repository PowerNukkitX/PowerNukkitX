package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement35 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_35");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement35() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement35(BlockState blockstate) {
        super(blockstate);
    }
}