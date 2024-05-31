package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(LIME_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}