package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcrete(BlockState blockstate) {
        super(blockstate);
    }
}