package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}