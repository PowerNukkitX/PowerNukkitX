package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedFence extends BlockFenceNonFlammable {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFence() {
        this(PROPERTIES.getDefaultState());
    }

    public static final BlockDefinition DEFINITION = BlockFenceNonFlammable.DEFINITION.toBuilder()
            .burnChance(-1)
            .build();

    public BlockWarpedFence(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }
}