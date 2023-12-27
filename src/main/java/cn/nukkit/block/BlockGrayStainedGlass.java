package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}