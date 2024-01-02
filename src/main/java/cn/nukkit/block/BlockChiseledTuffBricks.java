package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuffBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_TUFF_BRICKS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledTuffBricks(BlockState blockstate) {
        super(blockstate);
    }
}