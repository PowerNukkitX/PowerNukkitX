package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGlowingobsidian extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLOWINGOBSIDIAN);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowingobsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGlowingobsidian(BlockState blockstate) {
        super(blockstate);
    }
}