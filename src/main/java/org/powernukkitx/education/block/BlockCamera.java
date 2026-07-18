package org.powernukkitx.education.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCamera extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CAMERA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCamera() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCamera(BlockState blockstate) {
        super(blockstate);
    }
}