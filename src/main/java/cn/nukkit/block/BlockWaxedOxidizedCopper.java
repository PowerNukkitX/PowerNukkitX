package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopper extends BlockOxidizedCopper {
    public static final BlockProperties $1 = new BlockProperties(WAXED_OXIDIZED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedOxidizedCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedOxidizedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Waxed Oxidized Copper";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isWaxed() {
        return true;
    }
}