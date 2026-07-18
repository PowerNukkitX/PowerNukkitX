package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoker extends BlockLitSmoker {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOKER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockLitFurnace.DEFINITION.toBuilder()
            .lightEmission(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoker(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Smoker";
    }

    }
