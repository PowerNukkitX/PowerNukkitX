package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.property.CommonBlockProperties;

public class BlockCrimsonFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FENCE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }

    @Override
    public int getBurnChance() {
        return -1;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
