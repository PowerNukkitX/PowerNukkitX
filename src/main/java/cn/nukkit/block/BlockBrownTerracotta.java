package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_TERRACOTTA);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}