package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.block.definition.BlockDefinitions;
import cn.nukkit.math.AxisAlignedBB;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockFlowable extends BlockTransparent {
    public BlockFlowable(BlockState blockState) {
        super(blockState, BlockDefinitions.FLOWABLE);
    }

    public BlockFlowable(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }
}
