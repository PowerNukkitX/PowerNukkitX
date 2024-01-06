package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}