package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedStoneBricks extends BlockStoneBricks {

    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_STONE_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedStoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedStoneBricks(BlockState blockstate) {
        super(blockstate);
    }

}
