package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockQuartzBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(QUARTZ_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.8)
            .resistance(4)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Quartz Bricks";
    }

    }