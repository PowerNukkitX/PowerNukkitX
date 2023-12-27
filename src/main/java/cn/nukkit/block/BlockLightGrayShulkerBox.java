package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}