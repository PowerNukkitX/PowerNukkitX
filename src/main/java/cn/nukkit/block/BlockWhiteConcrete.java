package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcrete(BlockState blockstate) {
        super(blockstate);
    }
}