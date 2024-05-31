package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAcaciaFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(ACACIA_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaFence(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Acacia Fence";
    }
}