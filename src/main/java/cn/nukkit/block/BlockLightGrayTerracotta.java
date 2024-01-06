package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}