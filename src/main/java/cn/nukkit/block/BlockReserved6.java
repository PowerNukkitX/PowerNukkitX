package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockReserved6 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESERVED6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockReserved6() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockReserved6(BlockState blockstate) {
        super(blockstate);
    }
}