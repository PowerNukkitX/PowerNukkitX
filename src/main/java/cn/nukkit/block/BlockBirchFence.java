package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBirchFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_fence");

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
}