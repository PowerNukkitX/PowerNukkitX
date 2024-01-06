package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}