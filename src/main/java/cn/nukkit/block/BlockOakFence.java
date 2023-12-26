package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOakFence extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oak_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakFence(BlockState blockstate) {
        super(blockstate);
    }
}