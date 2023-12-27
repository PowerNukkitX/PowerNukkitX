package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanWool(BlockState blockstate) {
        super(blockstate);
    }
}