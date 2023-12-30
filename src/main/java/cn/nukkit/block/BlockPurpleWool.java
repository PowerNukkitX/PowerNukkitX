package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}