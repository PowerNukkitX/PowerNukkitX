package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCutCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCutCopper(BlockState blockstate) {
        super(blockstate);
    }
}