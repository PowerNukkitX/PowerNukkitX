package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCarpet(BlockState blockstate) {
        super(blockstate);
    }
}