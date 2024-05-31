package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockBlueIce extends BlockPackedIce {
    public static final BlockProperties $1 = new BlockProperties(BLUE_ICE);
    /**
     * @deprecated 
     */
    

    public BlockBlueIce() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueIce(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Blue Ice";
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getFrictionFactor() {
        return 0.989;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 14;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 4;
    }
}
