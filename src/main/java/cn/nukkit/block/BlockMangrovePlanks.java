package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangrovePlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangrovePlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangrovePlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mangrove Planks";
    }
}