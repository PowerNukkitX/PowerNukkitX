package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBrownStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}