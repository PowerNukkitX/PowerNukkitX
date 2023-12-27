package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}