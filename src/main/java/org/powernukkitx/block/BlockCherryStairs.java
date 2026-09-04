package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairsWood.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(3)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cherry Wood Stairs";
    }

    }