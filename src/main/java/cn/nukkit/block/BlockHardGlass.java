package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hard_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardGlass(BlockState blockstate) {
        super(blockstate);
    }
}