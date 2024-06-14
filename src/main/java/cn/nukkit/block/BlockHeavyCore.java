package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHeavyCore extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(HEAVY_CORE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHeavyCore() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHeavyCore(BlockState blockstate) {
        super(blockstate);
    }
}