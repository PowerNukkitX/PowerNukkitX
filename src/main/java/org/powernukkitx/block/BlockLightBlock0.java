package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock0 extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_0);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0)
            .resistance(0)
            .canPassThrough(true)
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock0() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock0(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockLightBlock0(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    }