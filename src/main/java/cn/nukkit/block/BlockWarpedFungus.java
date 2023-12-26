package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedFungus extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFungus(BlockState blockstate) {
        super(blockstate);
    }
}