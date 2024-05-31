package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedRoots extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties $1 = new BlockProperties(WARPED_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedRoots() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Warped Roots";
    }
}