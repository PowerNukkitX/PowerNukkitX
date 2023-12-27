package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_deepslate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}