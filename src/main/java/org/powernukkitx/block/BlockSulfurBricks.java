package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSulfurBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SULFUR_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSulfurBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSulfurBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    }