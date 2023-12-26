package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHangingRoots extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hanging_roots");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHangingRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHangingRoots(BlockState blockstate) {
        super(blockstate);
    }
}