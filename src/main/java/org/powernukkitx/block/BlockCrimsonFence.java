package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFence extends BlockFenceNonFlammable {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFence() {
        this(PROPERTIES.getDefaultState());
    }

    public static final BlockDefinition DEFINITION = BlockFenceNonFlammable.DEFINITION.toBuilder()
            .burnChance(-1)
            .build();

    public BlockCrimsonFence(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }
}