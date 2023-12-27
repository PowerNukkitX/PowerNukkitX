package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedDiorite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_diorite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDiorite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDiorite(BlockState blockstate) {
        super(blockstate);
    }
}