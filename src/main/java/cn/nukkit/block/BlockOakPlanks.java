package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakPlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(OAK_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOakPlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOakPlanks(BlockState blockstate) {
        super(blockstate);
    }
}