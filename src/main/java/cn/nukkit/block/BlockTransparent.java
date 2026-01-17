package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.block.definition.BlockDefinitions;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {
    public BlockTransparent(BlockState blockState) {
        super(blockState, BlockDefinitions.TRANSPARENT);
    }

    public BlockTransparent(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
