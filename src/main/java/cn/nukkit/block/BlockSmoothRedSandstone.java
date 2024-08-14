package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSmoothRedSandstone extends BlockSmoothSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_RED_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothRedSandstone(BlockState blockstate) {
        super(blockstate);
    }
}
