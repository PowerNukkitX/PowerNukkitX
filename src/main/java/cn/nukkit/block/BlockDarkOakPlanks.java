package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDarkOakPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}