package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(GREEN_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}