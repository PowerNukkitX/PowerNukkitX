package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedNylium extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_nylium");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedNylium() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedNylium(BlockState blockstate) {
        super(blockstate);
    }
}