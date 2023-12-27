package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}