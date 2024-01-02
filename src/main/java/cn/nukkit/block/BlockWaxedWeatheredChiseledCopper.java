package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredChiseledCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_CHISELED_COPPER);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}