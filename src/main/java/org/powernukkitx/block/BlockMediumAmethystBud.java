package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMediumAmethystBud extends BlockAmethystBud {
    public static final BlockProperties PROPERTIES = new BlockProperties(MEDIUM_AMETHYST_BUD, CommonBlockProperties.MINECRAFT_BLOCK_FACE);
    public static final BlockDefinition DEFINITION = BlockAmethystBud.DEFINITION.toBuilder()
            .lightEmission(2)
            .build();

    public BlockMediumAmethystBud() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMediumAmethystBud(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    protected String getNamePrefix() {
        return "Medium";
    }

    }
