package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_COBBLESTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(10)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyCobblestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyCobblestone(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Mossy Cobblestone";
    }

    }