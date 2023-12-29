package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLimeWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}