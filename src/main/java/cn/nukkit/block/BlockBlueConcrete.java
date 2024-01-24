package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueConcrete(BlockState blockstate) {
        super(blockstate);
    }
}