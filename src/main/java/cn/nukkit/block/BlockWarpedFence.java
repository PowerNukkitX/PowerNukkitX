package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinitions;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFence(BlockState blockState) {
        super(blockState, BlockDefinitions.NON_FLAMMABLE_FENCE);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }
}