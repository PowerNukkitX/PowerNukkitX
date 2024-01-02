package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrick extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrick() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrick(BlockState blockstate) {
        super(blockstate);
    }
}