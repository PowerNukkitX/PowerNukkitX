package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockSpruceFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Fence";
    }
}
