package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}