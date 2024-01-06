package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCherryFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }
}