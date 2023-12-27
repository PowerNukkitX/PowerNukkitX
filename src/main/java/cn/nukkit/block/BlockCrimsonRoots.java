package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonRoots extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_roots");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonRoots(BlockState blockstate) {
        super(blockstate);
    }
}