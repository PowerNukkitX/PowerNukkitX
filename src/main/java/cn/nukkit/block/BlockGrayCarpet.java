package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(GRAY_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}