package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockYellowStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}