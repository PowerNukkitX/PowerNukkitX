package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement97 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_97");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement97() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement97(BlockState blockstate) {
        super(blockstate);
    }
}