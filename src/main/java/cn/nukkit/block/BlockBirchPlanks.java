package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBirchPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchPlanks(BlockState blockstate) {
        super(blockstate);
    }
}