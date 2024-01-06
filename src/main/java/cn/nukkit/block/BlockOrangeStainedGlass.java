package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}