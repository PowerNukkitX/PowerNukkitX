package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowCarpet(BlockState blockstate) {
        super(blockstate);
    }
}