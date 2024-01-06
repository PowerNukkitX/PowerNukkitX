package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcrete(BlockState blockstate) {
        super(blockstate);
    }
}