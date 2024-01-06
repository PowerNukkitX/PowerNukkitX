package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockCyanStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}