package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperGrate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_copper_grate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}