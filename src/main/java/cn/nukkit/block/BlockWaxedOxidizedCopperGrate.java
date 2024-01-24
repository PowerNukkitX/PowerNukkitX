package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}