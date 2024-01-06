package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcrete extends BlockConcrete {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaConcrete(BlockState blockstate) {
        super(blockstate);
    }
}