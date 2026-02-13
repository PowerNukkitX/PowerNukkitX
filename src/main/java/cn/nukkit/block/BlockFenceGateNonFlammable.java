package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;

public class BlockFenceGateNonFlammable extends BlockFenceGate {
    public static final BlockDefinition DEFINITION = BlockFenceGate.DEFINITION.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    public BlockFenceGateNonFlammable(BlockState blockState) {
        this(blockState, DEFINITION);
    }

    public BlockFenceGateNonFlammable(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }
}
