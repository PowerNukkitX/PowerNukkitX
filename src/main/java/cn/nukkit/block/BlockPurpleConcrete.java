package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleConcrete(BlockState blockstate) {
        super(blockstate);
    }
}