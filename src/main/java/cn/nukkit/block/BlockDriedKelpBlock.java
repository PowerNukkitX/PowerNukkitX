package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockDriedKelpBlock extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(DRIED_KELP_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDriedKelpBlock() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDriedKelpBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Dried Kelp Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5F;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5;
    }

}
