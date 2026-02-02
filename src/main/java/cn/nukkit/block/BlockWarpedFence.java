package cn.nukkit.block;

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

    public BlockWarpedFence(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }
}