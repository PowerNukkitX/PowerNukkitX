package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockJunglePlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(JUNGLE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockJunglePlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockJunglePlanks(BlockState blockstate) {
        super(blockstate);
    }
}