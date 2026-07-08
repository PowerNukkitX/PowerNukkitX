package org.powernukkitx.block;

import org.powernukkitx.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}