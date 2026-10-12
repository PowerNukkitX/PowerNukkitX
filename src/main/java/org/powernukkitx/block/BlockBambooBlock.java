package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockBambooBlock extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_BLOCK, PILLAR_AXIS);
    public static final BlockDefinition DEFINITION = BlockLog.DEFINITION.toBuilder()
            .resistance(15)
            .burnAbility(20)
            .build();

    public BlockBambooBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public  BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Bamboo Block";
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedBambooBlock.PROPERTIES.getDefaultState();
    }
}