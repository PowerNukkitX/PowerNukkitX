package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakFence(BlockState blockstate) {
        super(blockstate);
    }
}