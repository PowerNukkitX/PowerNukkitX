package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockFletchingTable extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(FLETCHING_TABLE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.5)
            .resistance(12.5)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .canHarvestWithHand(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFletchingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFletchingTable(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Fletching Table";
    }

    }
