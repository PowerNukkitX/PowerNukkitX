package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}