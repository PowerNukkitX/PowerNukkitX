package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}