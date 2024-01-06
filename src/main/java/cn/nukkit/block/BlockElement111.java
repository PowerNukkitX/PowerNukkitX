package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement111 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_111");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement111() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement111(BlockState blockstate) {
        super(blockstate);
    }
}