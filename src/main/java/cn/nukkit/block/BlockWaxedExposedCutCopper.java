package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopper extends BlockExposedCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Cut Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}