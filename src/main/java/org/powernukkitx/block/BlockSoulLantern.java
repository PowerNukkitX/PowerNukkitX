package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.HANGING;

public class BlockSoulLantern extends BlockLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_LANTERN, HANGING);
    public static final BlockDefinition DEFINITION = BlockLantern.DEFINITION.toBuilder()
            .lightEmission(10)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulLantern(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Soul Lantern";
    }

    
}
