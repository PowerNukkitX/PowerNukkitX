package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangroveFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mangrove Fence";
    }
}