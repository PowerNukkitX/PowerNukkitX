package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueCarpet(BlockState blockstate) {
        super(blockstate);
    }
}