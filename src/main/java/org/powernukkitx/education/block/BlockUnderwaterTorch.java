package org.powernukkitx.education.block;

import org.powernukkitx.block.*;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTorch;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnderwaterTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNDERWATER_TORCH,  CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnderwaterTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnderwaterTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }
}