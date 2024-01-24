package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}