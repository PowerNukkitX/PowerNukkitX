package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCherryPlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(CHERRY_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryPlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryPlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cherry Planks";
    }

}