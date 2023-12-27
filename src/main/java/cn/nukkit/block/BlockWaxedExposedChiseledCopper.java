package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_chiseled_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}