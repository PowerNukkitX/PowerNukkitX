package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tuff_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBricks(BlockState blockstate) {
        super(blockstate);
    }
}