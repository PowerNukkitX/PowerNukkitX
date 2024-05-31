package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuffBricks extends Block {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_TUFF_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledTuffBricks(BlockState blockstate) {
        super(blockstate);
    }
}