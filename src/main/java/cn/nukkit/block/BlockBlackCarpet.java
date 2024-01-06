package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCarpet(BlockState blockstate) {
        super(blockstate);
    }
}