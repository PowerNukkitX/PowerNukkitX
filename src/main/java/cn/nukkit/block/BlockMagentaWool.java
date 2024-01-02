package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}