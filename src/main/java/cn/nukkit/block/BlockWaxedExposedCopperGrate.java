package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}