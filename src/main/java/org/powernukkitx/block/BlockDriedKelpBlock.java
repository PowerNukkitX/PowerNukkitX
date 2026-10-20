package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

public class BlockDriedKelpBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIED_KELP_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.5f)
            .resistance(2.5f)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDriedKelpBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDriedKelpBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Dried Kelp Block";
    }

}
