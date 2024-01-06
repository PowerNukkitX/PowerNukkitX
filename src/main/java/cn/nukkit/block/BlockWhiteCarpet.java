package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteCarpet(BlockState blockstate) {
        super(blockstate);
    }
}