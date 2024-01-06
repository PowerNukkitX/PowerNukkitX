package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangroveFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }
}