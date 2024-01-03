package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBlackWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}