package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkTerracotta extends BlockTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}