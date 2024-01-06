package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement110 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_110");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement110() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement110(BlockState blockstate) {
        super(blockstate);
    }
}