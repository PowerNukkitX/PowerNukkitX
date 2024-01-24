package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement25 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_25");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement25() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement25(BlockState blockstate) {
        super(blockstate);
    }
}