package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooMosaicStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_MOSAIC_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(3)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooMosaicStairs(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public String getName() {
        return "Bamboo Mosaic Stairs";
    }

    }