package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopper extends BlockCopperBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Block of Copper";
    }


    @Override
    public boolean isWaxed() {
        return true;
    }
}