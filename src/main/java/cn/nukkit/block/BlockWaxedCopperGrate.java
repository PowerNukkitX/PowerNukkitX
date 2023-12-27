package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_copper_grate");

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