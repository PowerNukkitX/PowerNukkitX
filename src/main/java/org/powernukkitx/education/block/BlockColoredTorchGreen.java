package org.powernukkitx.education.block;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTorch;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchGreen extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(COLORED_TORCH_GREEN,  CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockColoredTorchGreen() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockColoredTorchGreen(BlockState blockstate) {
        super(blockstate);
    }
}