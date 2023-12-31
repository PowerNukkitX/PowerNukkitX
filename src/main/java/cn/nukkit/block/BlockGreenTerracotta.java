package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}