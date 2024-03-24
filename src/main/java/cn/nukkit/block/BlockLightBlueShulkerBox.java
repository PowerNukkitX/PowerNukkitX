package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_SHULKER_BOX);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}