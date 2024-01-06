package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}