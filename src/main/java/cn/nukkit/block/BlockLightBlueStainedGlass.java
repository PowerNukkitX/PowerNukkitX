package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}