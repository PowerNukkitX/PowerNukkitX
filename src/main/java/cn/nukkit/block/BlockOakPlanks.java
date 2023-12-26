package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockOakPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oak_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}