package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstone extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE);
    public static final BlockDefinition DEFINITION = BlockBlackstone.DEFINITION.toBuilder()
            .hardness(1.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstone(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Polished Blackstone";
    }

    
    }