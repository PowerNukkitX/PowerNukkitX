package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock5 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_5);
    public static final BlockDefinition DEFINITION = BlockLightBlock0.DEFINITION.toBuilder()
            .lightEmission(5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock5() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock5(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
}