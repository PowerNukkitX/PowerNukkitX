package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(WHITE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteCarpet(BlockState blockstate) {
        super(blockstate);
    }
}