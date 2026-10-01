package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock11 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_11);
    public static final BlockDefinition DEFINITION = BlockLightBlock0.DEFINITION.toBuilder()
            .lightEmission(11)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock11() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock11(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
}