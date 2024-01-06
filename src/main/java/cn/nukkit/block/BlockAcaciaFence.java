package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAcaciaFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Fence";
    }
}