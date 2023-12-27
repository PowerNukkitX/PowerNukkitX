package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedRoots extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_roots");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedRoots(BlockState blockstate) {
        super(blockstate);
    }
}