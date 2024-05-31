package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopper extends BlockCopperBlock {
    public static final BlockProperties $1 = new BlockProperties(WAXED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Block of Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}