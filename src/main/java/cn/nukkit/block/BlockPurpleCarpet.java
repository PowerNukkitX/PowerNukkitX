package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCarpet(BlockState blockstate) {
        super(blockstate);
    }
}