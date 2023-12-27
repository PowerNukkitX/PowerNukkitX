package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}