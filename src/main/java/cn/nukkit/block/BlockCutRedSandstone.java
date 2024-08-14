package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCutRedSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CUT_RED_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCutRedSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCutRedSandstone(BlockState blockstate) {
        super(blockstate);
    }
}
