package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedWarpedStem extends BlockStemStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_WARPED_STEM, CommonBlockProperties.PILLAR_AXIS);
    public static final BlockDefinition DEFINITION = BlockStemStripped.DEFINITION.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedWarpedStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedWarpedStem(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Stripped Warped Stem";
    }

    
    }