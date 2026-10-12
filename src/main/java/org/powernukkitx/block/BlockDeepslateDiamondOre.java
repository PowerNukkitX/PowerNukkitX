package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateDiamondOre extends BlockDiamondOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_DIAMOND_ORE);
    public static final BlockDefinition DEFINITION = BlockDiamondOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateDiamondOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Diamond Ore";
    }

    }