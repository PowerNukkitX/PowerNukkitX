package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockBambooFence extends BlockFence {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooFence() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBambooFence(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Bamboo Fence";
    }
}