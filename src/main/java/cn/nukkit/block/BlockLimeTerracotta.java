package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(LIME_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}