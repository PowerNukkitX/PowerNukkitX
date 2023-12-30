package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPinkWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}