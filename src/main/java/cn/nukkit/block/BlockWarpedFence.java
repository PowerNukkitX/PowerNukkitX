package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

}