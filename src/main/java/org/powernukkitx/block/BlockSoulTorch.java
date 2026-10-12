package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class BlockSoulTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_TORCH, TORCH_FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockTorch.DEFINITION.toBuilder()
            .lightEmission(10)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulTorch(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Soul Torch";
    }

    }
