package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHardenedClay extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HARDENED_CLAY);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.25)
            .resistance(7)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardenedClay() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardenedClay(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockHardenedClay(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    }