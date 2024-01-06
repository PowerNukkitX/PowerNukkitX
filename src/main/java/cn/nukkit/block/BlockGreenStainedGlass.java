package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGreenStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}