package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}