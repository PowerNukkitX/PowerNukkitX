package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}