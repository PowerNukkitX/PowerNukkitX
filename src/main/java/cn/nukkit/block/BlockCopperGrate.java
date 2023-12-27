package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}