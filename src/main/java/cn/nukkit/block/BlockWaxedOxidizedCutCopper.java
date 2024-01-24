package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCutCopper extends BlockOxidizedCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Cut Copper";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

}