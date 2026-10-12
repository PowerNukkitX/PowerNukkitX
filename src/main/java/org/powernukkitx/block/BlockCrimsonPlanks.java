package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_PLANKS);
    public static final BlockDefinition DEFINITION = BlockPlanks.DEFINITION.toBuilder()
            .resistance(3)
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonPlanks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crimson Planks";
    }

    }