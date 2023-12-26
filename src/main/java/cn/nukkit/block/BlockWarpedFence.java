package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFence(BlockState blockstate) {
        super(blockstate);
    }
}