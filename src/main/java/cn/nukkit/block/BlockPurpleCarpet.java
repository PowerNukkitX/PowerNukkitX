package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCarpet(BlockState blockstate) {
        super(blockstate);
    }
}