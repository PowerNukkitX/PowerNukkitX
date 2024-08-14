package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMossyStoneBricks extends BlockStoneBricks {

    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_STONE_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyStoneBricks(BlockState blockstate) {
        super(blockstate);
    }

}
