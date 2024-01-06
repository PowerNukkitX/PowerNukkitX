package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement27 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_27");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement27() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement27(BlockState blockstate) {
        super(blockstate);
    }
}