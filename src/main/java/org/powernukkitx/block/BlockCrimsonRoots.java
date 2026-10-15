package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonRoots extends BlockHanging implements BlockFlowerPot.FlowerPotBlock, Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_ROOTS);
    public static final BlockDefinition DEFINITION = BlockHanging.DEFINITION.toBuilder()
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonRoots(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }
}