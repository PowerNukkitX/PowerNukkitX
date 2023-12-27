package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}