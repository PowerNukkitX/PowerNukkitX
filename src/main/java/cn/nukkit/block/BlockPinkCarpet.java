package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCarpet(BlockState blockstate) {
        super(blockstate);
    }
}