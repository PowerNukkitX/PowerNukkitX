package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooPlanks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_PLANKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooPlanks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public String getName() {
        return "Bamboo Planks";
    }

    }