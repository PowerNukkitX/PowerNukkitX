package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBirchFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(BIRCH_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Birch Fence";
    }
}