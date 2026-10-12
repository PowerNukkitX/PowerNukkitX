package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock13 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_13);
    public static final BlockDefinition DEFINITION = BlockLightBlock0.DEFINITION.toBuilder()
            .lightEmission(13)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock13() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock13(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
}