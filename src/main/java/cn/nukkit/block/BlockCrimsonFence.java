package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinitions;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFence(BlockState blockState) {
        super(blockState, BlockDefinitions.NON_FLAMMABLE_FENCE);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }
}