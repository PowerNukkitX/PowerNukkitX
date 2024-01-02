package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CONCRETE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenConcrete(BlockState blockstate) {
        super(blockstate);
    }
}