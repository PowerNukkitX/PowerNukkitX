package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaTerracotta extends BlockHardenedClay {
    public static final BlockProperties $1 = new BlockProperties(MAGENTA_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}