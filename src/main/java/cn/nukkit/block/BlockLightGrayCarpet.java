package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCarpet(BlockState blockstate) {
        super(blockstate);
    }
}