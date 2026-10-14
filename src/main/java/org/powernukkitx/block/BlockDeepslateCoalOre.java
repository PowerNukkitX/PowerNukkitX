package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCoalOre extends BlockCoalOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_COAL_ORE);
    public static final BlockDefinition DEFINITION = BlockCoalOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCoalOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Coal Ore";
    }

    }