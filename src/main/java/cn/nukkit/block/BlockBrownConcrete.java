package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcrete(BlockState blockstate) {
        super(blockstate);
    }
}