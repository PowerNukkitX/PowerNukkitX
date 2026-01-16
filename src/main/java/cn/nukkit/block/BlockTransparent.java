package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {
    public BlockTransparent(BlockState blockState) {
        super(blockState);
    }

    public BlockTransparent(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
