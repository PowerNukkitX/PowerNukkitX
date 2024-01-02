package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_COPPER_GRATE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}