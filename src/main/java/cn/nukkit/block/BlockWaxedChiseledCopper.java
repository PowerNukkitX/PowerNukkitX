package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_CHISELED_COPPER);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}