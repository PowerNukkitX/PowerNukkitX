package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}