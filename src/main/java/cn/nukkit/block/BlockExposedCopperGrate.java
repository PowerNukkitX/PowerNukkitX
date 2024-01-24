package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}