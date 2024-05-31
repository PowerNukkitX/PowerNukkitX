package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockTuffBricks extends Block {
    public static final BlockProperties $1 = new BlockProperties(TUFF_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTuffBricks(BlockState blockstate) {
        super(blockstate);
    }
}