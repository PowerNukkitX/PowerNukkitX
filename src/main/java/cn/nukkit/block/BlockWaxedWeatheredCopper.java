package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopper extends BlockWeatheredCopper{
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}