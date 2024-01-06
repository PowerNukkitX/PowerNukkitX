package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}