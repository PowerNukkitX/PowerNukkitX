package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCutCopper extends BlockWeatheredCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
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