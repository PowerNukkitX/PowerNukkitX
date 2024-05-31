package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(CYAN_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}