package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}