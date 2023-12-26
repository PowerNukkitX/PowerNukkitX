package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFence(BlockState blockstate) {
        super(blockstate);
    }
}