package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);
    public static final BlockDefinition DEFINITION = BlockWoodenPressurePlate.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedPressurePlate(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Pressure Plate";
    }

    
    }