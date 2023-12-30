package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeConcrete(BlockState blockstate) {
        super(blockstate);
    }
}