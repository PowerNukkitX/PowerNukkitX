package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:nether_brick_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickFence(BlockState blockstate) {
        super(blockstate);
    }
}