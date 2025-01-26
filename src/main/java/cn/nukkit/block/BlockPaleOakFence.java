package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPaleOakFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pale Oak Fence";
    }
}