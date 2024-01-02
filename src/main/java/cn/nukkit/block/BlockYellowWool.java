package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockYellowWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}