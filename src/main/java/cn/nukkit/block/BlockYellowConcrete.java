package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowConcrete(BlockState blockstate) {
        super(blockstate);
    }
}