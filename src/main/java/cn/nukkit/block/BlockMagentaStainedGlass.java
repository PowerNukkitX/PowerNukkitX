package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}