package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCutCopper extends BlockCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Cut Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}