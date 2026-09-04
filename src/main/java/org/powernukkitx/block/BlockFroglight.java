package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

public abstract class BlockFroglight extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .resistance(0.3)
            .lightEmission(15)
            .build();

    public BlockFroglight(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockFroglight(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    
    }
