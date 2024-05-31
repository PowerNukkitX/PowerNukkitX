package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockFlowable extends BlockTransparent {
    /**
     * @deprecated 
     */
    
    public BlockFlowable(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }
}
