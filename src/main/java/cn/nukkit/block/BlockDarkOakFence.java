package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDarkOakFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Fence";
    }
}