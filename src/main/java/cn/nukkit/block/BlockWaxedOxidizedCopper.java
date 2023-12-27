package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopper extends Block {
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
}