package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }
}