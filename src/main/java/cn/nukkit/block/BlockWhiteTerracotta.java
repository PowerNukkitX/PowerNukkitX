package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(WHITE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}