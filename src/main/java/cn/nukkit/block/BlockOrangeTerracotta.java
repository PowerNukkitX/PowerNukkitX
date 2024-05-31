package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(ORANGE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}