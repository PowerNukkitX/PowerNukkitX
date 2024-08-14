package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDarkPrismarine extends BlockPrismarine {

    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_PRISMARINE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkPrismarine() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDarkPrismarine(BlockState blockState) {
        super(blockState);
    }
}
