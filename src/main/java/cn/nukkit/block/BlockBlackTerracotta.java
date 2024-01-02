package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_TERRACOTTA);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}