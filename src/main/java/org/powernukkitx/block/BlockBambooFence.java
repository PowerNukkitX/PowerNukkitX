package org.powernukkitx.block;


import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockBambooFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooFence(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Fence";
    }
}
