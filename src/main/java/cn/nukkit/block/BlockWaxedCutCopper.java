package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}