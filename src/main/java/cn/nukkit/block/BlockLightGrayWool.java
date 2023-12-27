package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayWool(BlockState blockstate) {
        super(blockstate);
    }
}