package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayWool(BlockState blockstate) {
        super(blockstate);
    }
}