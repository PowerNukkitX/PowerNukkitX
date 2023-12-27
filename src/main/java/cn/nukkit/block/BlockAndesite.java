package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAndesite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:andesite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAndesite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAndesite(BlockState blockstate) {
        super(blockstate);
    }
}