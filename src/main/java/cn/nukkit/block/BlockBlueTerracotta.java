package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}