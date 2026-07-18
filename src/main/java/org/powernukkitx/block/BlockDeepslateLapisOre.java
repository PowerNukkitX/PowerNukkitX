package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateLapisOre extends BlockLapisOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_LAPIS_ORE);
    public static final BlockDefinition DEFINITION = BlockLapisOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateLapisOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateLapisOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Ore";
    }

    }