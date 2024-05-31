package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(BLACK_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackCarpet(BlockState blockstate) {
        super(blockstate);
    }
}