package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackConcrete(BlockState blockstate) {
        super(blockstate);
    }
}