package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockRedStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_STAINED_GLASS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}