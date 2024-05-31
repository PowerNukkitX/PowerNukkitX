package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCutCopper extends BlockWeatheredCutCopper {
    public static final BlockProperties $1 = new BlockProperties(WAXED_WEATHERED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCutCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Weathered Cut Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}