package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_0, CommonBlockProperties.BLOCK_LIGHT_LEVEL);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0)
            .resistance(0)
            .canPassThrough(true)
            .canBeReplaced(true)
            .canHarvestWithHand(false)
            .canBeFlowedInto(true)
            .waterloggingLevel(2)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public int getLightLevel() {
        return getPropertyValue(CommonBlockProperties.BLOCK_LIGHT_LEVEL);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    }