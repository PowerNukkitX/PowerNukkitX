package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

public class BlockDriedKelpBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIED_KELP_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.5F)
            .resistance(2.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDriedKelpBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDriedKelpBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Dried Kelp Block";
    }

}
