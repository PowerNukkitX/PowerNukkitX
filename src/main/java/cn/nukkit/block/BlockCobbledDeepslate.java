package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cobbled_deepslate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}