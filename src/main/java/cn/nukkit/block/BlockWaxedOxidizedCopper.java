package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopper extends BlockOxidizedCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_oxidized_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCopper(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Waxed Oxidized Copper";
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}