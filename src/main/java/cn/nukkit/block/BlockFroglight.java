package cn.nukkit.block;

public abstract class BlockFroglight extends BlockSolid {
    /**
     * @deprecated 
     */
    

    public BlockFroglight(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.3;
    }
}
