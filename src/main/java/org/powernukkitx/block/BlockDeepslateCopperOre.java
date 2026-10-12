package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCopperOre extends BlockCopperOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_COPPER_ORE);
    public static final BlockDefinition DEFINITION = BlockCopperOre.DEFINITION.toBuilder()
            .hardness(4.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateCopperOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Copper Ore";
    }

    }