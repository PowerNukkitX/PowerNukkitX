package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class BlockCopperTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_TORCH, TORCH_FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockTorch.DEFINITION.toBuilder()
            .lightEmission(14)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperTorch(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Copper Torch";
    }

    }
