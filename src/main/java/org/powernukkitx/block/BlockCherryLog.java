package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;


public class BlockCherryLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_LOG, CommonBlockProperties.PILLAR_AXIS);
    public static final BlockDefinition DEFINITION = BlockLog.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(10)
            .burnChance(5)
            .burnAbility(5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryLog() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockCherryLog(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cherry log";
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedCherryLog.PROPERTIES.getDefaultState();
    }
}

