package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcrete(BlockState blockstate) {
        super(blockstate);
    }
}