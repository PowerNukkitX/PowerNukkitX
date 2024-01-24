package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
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