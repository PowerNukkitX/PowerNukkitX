package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBrownWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}