package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_SANDSTONE);
    public static final BlockDefinition DEFINITION = BlockSandstone.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(6)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothSandstone(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockSmoothSandstone(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    
    }
