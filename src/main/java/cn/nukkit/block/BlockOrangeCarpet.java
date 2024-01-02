package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CARPET);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}