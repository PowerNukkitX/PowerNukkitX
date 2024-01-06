package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockInfoUpdate2 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(INFO_UPDATE2);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInfoUpdate2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInfoUpdate2(BlockState blockstate) {
        super(blockstate);
    }
}