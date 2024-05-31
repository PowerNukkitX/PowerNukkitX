package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(BROWN_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}