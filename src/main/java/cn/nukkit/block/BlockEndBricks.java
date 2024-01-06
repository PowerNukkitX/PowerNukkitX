package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockEndBricks extends BlockEndStone {
    public static final BlockProperties PROPERTIES = new BlockProperties(END_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "End Stone Bricks";
    }
}