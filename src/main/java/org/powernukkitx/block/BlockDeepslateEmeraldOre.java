package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateEmeraldOre extends BlockEmeraldOre  {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_EMERALD_ORE);
    public static final BlockDefinition DEFINITION = BlockEmeraldOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateEmeraldOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Emerald Ore";
    }

    }