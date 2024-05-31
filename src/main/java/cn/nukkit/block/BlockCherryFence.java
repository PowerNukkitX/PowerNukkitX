package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCherryFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(CHERRY_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cherry Fence";
    }
}