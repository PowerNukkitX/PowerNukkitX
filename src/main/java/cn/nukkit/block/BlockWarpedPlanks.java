package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedPlanks(BlockState blockstate) {
        super(blockstate);
    }
}