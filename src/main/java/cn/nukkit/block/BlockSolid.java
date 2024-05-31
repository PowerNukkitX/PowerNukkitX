package cn.nukkit.block;

import cn.nukkit.math.BlockFace;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSolid extends Block {
    /**
     * @deprecated 
     */
    

    public BlockSolid(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return true;
    }

}
