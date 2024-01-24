package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}