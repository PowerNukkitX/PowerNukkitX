package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(GREEN_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenCarpet(BlockState blockstate) {
        super(blockstate);
    }
}