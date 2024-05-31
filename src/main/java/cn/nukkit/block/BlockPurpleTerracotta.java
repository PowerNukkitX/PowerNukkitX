package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(PURPLE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}