package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}