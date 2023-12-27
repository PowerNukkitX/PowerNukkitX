package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockEndBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:end_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndBricks(BlockState blockstate) {
        super(blockstate);
    }
}