package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

public class BlockBambooMosaic extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_MOSAIC);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(15)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooMosaic() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooMosaic(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public String getName() {
        return "Bamboo Mosaic";
    }

    }