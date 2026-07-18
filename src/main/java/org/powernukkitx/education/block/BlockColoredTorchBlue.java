package org.powernukkitx.education.block;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTorch;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchBlue extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(COLORED_TORCH_BLUE,  CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockColoredTorchBlue() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockColoredTorchBlue(BlockState blockstate) {
        super(blockstate);
    }
}