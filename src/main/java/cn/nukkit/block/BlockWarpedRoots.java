package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedRoots extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_roots");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedRoots(BlockState blockstate) {
        super(blockstate);
    }
}