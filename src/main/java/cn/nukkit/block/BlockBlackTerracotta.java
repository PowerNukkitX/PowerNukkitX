package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(BLACK_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}