package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

public class BlockBlueIce extends BlockPackedIce {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_ICE);
    public static final BlockDefinition DEFINITION = BlockPackedIce.DEFINITION.toBuilder()
            .hardness(2.8)
            .resistance(14)
            .lightEmission(4)
            .build();

    public BlockBlueIce() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockBlueIce(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
    @Override
    public String getName() {
        return "Blue Ice";
    }
    
    @Override
    public double getFrictionFactor() {
        return 0.989;
    }

    @Override
    public boolean isTransparent() {
        return false;
    }

    }
