package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrick extends BlockNetherBrick {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrick() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrick(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Red Nether Bricks";
    }
}