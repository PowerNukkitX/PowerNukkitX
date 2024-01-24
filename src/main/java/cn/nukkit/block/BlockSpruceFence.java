package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSpruceFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Fence";
    }
}