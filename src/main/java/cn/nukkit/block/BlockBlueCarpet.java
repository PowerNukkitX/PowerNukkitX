package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCarpet(BlockState blockstate) {
        super(blockstate);
    }
}