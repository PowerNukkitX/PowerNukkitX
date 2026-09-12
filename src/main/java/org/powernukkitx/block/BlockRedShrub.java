package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedShrub extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SHRUB);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedShrub() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockRedShrub(BlockState blockstate) {
        super(blockstate);
    }
}
