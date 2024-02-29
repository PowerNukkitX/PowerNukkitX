package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockFlowable extends BlockTransparent {
    public BlockFlowable(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }
}
