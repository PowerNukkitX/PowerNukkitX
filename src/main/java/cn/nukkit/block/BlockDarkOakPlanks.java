package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDarkOakPlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(DARK_OAK_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDarkOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDarkOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}