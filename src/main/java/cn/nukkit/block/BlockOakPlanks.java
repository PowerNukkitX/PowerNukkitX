package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}