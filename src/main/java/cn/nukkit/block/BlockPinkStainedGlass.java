package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPinkStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}