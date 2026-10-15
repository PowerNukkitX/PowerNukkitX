package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

public abstract class BlockFenceNonFlammable extends BlockFence {
    public static final BlockDefinition DEFINITION = BlockFence.DEFINITION.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    public BlockFenceNonFlammable(BlockState blockState) {
        this(blockState, DEFINITION);
    }

    public BlockFenceNonFlammable(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
