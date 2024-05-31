package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(BROWN_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownCarpet(BlockState blockstate) {
        super(blockstate);
    }
}