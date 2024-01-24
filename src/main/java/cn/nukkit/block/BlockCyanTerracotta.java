package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}