package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGrayStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}