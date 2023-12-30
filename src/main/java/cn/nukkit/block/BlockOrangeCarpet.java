package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}