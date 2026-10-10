package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedRoots extends BlockHanging implements BlockFlowerPot.FlowerPotBlock, Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_ROOTS);
    public static final BlockDefinition DEFINITION = BlockHanging.DEFINITION.toBuilder()
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedRoots(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }
}