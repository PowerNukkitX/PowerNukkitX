package cn.nukkit.block;

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

    public BlockCrimsonFence(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }
}