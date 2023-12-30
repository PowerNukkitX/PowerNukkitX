package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGreenWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}