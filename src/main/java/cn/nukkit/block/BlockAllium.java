package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAllium extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(ALLIUM);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAllium() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockAllium(BlockState blockstate) {
        super(blockstate);
    }
}