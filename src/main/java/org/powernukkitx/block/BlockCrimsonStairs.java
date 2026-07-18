package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairsWood.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crimson Wood Stairs";
    }

    
    }