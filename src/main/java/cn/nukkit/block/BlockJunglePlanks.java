package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockJunglePlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJunglePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJunglePlanks(BlockState blockstate) {
        super(blockstate);
    }
}