package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}