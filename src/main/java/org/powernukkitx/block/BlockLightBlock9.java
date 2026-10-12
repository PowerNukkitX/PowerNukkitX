package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock9 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_9);
    public static final BlockDefinition DEFINITION = BlockLightBlock0.DEFINITION.toBuilder()
            .lightEmission(9)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock9() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock9(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
}