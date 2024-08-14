package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothSandstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }
}
