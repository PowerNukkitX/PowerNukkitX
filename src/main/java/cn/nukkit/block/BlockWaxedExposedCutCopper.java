package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopper extends BlockExposedCutCopper {
    public static final BlockProperties $1 = new BlockProperties(WAXED_EXPOSED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Exposed Cut Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}