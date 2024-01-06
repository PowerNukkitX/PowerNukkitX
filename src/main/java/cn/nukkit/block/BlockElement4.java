package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement4 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_4");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement4(BlockState blockstate) {
        super(blockstate);
    }
}