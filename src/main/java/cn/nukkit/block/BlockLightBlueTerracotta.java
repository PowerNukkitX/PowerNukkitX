package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}