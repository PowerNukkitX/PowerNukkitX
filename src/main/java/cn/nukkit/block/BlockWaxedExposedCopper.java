package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopper extends BlockExposedCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}