package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmallAmethystBud extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMALL_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);
    public static final BlockDefinition DEFINITION = BlockAmethystBud.DEFINITION.toBuilder()
            .lightEmission(1)
            .build();

    public BlockSmallAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmallAmethystBud(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected String getNamePrefix() {
        return "Small";
    }

    }
