package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedSand extends BlockSand {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_SAND);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedSand() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockRedSand(BlockState blockState) {
        super(blockState);
    }
}
