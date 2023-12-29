package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueTerracotta extends BlockTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}