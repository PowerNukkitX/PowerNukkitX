package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSprucePlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSprucePlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSprucePlanks(BlockState blockstate) {
        super(blockstate);
    }
}