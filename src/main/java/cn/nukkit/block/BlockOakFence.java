package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oak Fence";
    }
}