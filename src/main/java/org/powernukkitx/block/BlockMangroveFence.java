package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockMangroveFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }
}
