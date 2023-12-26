package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}