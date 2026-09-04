package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateGoldOre extends BlockGoldOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_GOLD_ORE);
    public static final BlockDefinition DEFINITION = BlockGoldOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateGoldOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Gold Ore";
    }

    }