package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockShortGrass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHORT_GRASS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockShortGrass() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockShortGrass(BlockState blockstate) {
        super(blockstate);
    }
}