package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPaleOakPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}