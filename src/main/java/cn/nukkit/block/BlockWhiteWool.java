package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}