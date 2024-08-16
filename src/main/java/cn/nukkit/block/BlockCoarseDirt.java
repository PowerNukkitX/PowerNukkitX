package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCoarseDirt extends BlockDirt {
    public static final BlockProperties PROPERTIES = new BlockProperties(COARSE_DIRT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoarseDirt() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoarseDirt(BlockState blockstate) {
        super(blockstate);
    }
}