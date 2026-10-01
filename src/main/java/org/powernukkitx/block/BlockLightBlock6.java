package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock6 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_6);
    public static final BlockDefinition DEFINITION = BlockLightBlock0.DEFINITION.toBuilder()
            .lightEmission(6)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock6() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock6(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
}