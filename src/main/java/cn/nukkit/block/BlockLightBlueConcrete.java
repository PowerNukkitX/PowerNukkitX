package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_CONCRETE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueConcrete(BlockState blockstate) {
        super(blockstate);
    }
}