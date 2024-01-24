package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}