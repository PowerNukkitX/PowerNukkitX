package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_GRATE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}