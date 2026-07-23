package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

public class BlockBlastFurnace extends BlockLitBlastFurnace {

    public static final BlockProperties PROPERTIES = new BlockProperties(BLAST_FURNACE, MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockLitFurnace.DEFINITION.toBuilder()
            .lightEmission(0)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlastFurnace(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    }
