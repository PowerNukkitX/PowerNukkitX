package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockBambooMosaic extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO_MOSAIC);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooMosaic() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooMosaic(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Bamboo Mosaic";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }
}