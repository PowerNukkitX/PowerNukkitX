package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(BLUE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}