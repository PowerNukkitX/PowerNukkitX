package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class BlockSulfurSpike extends BlockPointedDripstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SULFUR_SPIKE, DRIPSTONE_THICKNESS, HANGING);
    public static final BlockDefinition DEFINITION = BlockPointedDripstone.DEFINITION.toBuilder()
            .hardness(1.5)
            .resistance(3)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSulfurSpike() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSulfurSpike(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sulfur Spike";
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}