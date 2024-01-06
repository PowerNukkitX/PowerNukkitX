package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcrete(BlockState blockstate) {
        super(blockstate);
    }
}