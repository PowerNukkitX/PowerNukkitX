package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCutSandstone extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CUT_SANDSTONE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCutSandstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCutSandstone(BlockState blockstate) {
        super(blockstate);
    }
}
