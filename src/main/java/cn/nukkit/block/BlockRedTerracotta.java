package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(RED_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}