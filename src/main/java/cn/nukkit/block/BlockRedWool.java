package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockRedWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}