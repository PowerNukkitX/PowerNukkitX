package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBirchFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_FENCE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Fence";
    }
}