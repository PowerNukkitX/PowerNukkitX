package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

public class BlockReinforcedDeepslate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(REINFORCED_DEEPSLATE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .resistance(1200.0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockReinforcedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockReinforcedDeepslate(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "ReinForced DeepSlate";
    }

    }
