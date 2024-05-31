package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopper extends BlockExposedCopper {
    public static final BlockProperties $1 = new BlockProperties(WAXED_EXPOSED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}