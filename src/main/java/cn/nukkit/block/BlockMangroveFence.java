package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangroveFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFence(BlockState blockstate) {
        super(blockstate);
    }
}