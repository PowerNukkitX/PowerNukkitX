package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}