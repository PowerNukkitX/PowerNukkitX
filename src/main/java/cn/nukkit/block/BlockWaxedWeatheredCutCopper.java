package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCutCopper extends BlockWeatheredCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Weathered Cut Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}