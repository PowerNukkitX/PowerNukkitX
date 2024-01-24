package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}