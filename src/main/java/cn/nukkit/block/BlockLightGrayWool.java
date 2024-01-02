package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}