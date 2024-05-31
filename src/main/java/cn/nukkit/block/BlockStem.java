package cn.nukkit.block;

public abstract class BlockStem extends BlockLog {
    /**
     * @deprecated 
     */
    

    public BlockStem(BlockState blockstate) {
        super(blockstate);
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
        return 2;
    }
}
