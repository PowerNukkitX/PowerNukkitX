package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowCarpet(BlockState blockstate) {
        super(blockstate);
    }
}