package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGrayWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}