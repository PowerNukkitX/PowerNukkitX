package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPackedIce extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(PACKED_ICE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPackedIce() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPackedIce(BlockState blockstate) {
        super(blockstate);
    }
}