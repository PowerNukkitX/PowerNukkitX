package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_GRAY_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}