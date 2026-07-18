package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledPolishedBlackstone extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_POLISHED_BLACKSTONE);
    public static final BlockDefinition DEFINITION = BlockBlackstone.DEFINITION.toBuilder()
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledPolishedBlackstone(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Chiseled Polished Blackstone";
    }

    }