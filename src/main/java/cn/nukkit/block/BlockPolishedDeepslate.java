package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslate extends BlockCobbledDeepslate {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}