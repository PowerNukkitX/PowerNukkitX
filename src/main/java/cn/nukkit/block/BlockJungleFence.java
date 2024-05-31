package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockJungleFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(JUNGLE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockJungleFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockJungleFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Jungle Fence";
    }
}