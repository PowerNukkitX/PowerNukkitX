package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}