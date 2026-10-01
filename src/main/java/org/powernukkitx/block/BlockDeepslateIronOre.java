package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateIronOre extends BlockIronOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_IRON_ORE);
    public static final BlockDefinition DEFINITION = BlockIronOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateIronOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateIronOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Iron Ore";
    }

    }