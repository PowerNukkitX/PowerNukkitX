package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(PINK_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}