package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockCherryFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }
}
