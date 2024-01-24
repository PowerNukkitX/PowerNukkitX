package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkConcrete(BlockState blockstate) {
        super(blockstate);
    }
}