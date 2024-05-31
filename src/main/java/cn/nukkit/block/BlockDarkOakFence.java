package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDarkOakFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(DARK_OAK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDarkOakFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDarkOakFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Dark Oak Fence";
    }
}