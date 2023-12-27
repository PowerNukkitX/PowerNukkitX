package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopper(BlockState blockstate) {
        super(blockstate);
    }
}