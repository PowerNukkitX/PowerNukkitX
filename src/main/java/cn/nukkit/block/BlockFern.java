package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockFern extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(FERN);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFern() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockFern(BlockState blockstate) {
        super(blockstate);
    }
}