package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDiorite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:diorite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiorite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiorite(BlockState blockstate) {
        super(blockstate);
    }
}