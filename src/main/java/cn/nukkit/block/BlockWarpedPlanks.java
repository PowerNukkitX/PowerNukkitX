package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedPlanks extends BlockPlanks {
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