package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWeb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:web");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeb(BlockState blockstate) {
        super(blockstate);
    }
}