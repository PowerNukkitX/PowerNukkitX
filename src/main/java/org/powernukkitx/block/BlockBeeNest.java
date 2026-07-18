package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBeeNest extends BlockBeehive {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEE_NEST, CommonBlockProperties.DIRECTION, CommonBlockProperties.HONEY_LEVEL);
    public static final BlockDefinition DEFINITION = BlockBeehive.DEFINITION.toBuilder()
            .hardness(0.3)
            .resistance(1.5)
            .burnChance(30)
            .burnAbility(60)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeeNest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeeNest(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Bee Nest";
    }

    }
