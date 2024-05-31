package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSpruceFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Spruce Fence";
    }
}