package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.math.AxisAlignedBB;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockFlowable extends BlockTransparent {
    public static final BlockDefinition FLOWABLE = TRANSPARENT.toBuilder()
            .canBeFlowedInto(true)
            .canPassThrough(true)
            .hardness(0)
            .resistance(0)
            .isSolid(false)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .build();

    public BlockFlowable(BlockState blockState) {
        super(blockState, FLOWABLE);
    }

    public BlockFlowable(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }
}
