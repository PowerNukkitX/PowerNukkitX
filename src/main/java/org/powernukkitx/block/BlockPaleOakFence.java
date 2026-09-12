package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockPaleOakFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Oak Fence";
    }
}
