package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBlueWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}