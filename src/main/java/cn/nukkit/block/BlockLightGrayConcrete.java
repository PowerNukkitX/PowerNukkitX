package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}