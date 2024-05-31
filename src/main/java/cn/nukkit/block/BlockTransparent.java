package cn.nukkit.block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {
    /**
     * @deprecated 
     */
    
    public BlockTransparent(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

}
