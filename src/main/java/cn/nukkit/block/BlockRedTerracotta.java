package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}