package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement79 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_79");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement79() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement79(BlockState blockstate) {
        super(blockstate);
    }
}