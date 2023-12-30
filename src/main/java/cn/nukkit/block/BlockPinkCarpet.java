package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCarpet(BlockState blockstate) {
        super(blockstate);
    }
}