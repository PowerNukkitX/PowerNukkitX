package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCryingObsidian extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crying_obsidian");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCryingObsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCryingObsidian(BlockState blockstate) {
        super(blockstate);
    }
}