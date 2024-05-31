package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopper extends BlockWeatheredCopper{
    public static final BlockProperties $1 = new BlockProperties(WAXED_WEATHERED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Weathered Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}