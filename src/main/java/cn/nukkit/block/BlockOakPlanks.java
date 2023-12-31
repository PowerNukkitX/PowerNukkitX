package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakPlanks extends BlockPlanks {
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