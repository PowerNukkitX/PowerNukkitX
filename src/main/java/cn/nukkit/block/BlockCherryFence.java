package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCherryFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryFence(BlockState blockstate) {
        super(blockstate);
    }
}