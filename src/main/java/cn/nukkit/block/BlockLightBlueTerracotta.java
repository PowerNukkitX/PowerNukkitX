package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLUE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}