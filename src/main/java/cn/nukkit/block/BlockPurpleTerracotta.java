package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_TERRACOTTA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}