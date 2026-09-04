package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockPackedMud extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PACKED_MUD);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1)
            .resistance(3)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPackedMud() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPackedMud(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Packed Mud";
    }

    
    }
