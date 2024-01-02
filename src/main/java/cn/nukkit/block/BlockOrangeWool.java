package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}