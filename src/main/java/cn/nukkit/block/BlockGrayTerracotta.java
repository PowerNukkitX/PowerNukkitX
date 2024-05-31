package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(GRAY_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}