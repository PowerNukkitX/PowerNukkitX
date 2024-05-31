package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBirchPlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(BIRCH_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchPlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchPlanks(BlockState blockstate) {
        super(blockstate);
    }
}