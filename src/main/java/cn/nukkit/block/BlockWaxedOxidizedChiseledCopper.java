package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_CHISELED_COPPER);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}