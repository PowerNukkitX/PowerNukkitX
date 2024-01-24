package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcrete(BlockState blockstate) {
        super(blockstate);
    }
}