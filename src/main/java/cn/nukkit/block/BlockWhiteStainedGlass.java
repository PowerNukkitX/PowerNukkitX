package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}