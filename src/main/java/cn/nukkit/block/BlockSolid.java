package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.math.BlockFace;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockSolid extends Block {
    public static final BlockDefinition SOLID = DEFAULT_DEFINITION.toBuilder()
            .isSolid(true)
            .build();

    public BlockSolid(BlockState blockState) {
        super(blockState, SOLID);
    }

    public BlockSolid(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}