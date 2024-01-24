package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockJungleFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jungle Fence";
    }
}