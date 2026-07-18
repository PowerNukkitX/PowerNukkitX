package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCalcite extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(CALCITE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.75)
            .resistance(0.75)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockCalcite() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockCalcite(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Calcite";
    }

    }
