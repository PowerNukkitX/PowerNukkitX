package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOakFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(OAK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOakFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Oak Fence";
    }
}