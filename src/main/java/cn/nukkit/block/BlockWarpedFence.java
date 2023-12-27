package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFence(BlockState blockstate) {
        super(blockstate);
    }
}