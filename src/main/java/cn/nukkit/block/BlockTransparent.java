package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {
    public static final BlockDefinition TRANSPARENT = DEFAULT_DEFINITION.toBuilder()
            .isTransparent(true)
            .build();

    public BlockTransparent(BlockState blockState) {
        super(blockState, TRANSPARENT);
    }

    public BlockTransparent(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
