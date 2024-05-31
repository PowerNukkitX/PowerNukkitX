package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_GRAY_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}