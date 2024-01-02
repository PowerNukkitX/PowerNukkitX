package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGreenStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_STAINED_GLASS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}