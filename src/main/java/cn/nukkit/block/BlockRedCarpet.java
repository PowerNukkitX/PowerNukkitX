package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCarpet(BlockState blockstate) {
        super(blockstate);
    }
}