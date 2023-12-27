package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}