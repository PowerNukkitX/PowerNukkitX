package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oak_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakFence(BlockState blockstate) {
        super(blockstate);
    }
}