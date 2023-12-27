package cn.nukkit.block;

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