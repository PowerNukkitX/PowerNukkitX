package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

public abstract class BlockStem extends BlockLog {
    public static final BlockDefinition DEFINITION = BlockLog.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(2)
            .burnChance(0)
            .burnAbility(0)
            .build();

    public BlockStem(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockStem(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    }
