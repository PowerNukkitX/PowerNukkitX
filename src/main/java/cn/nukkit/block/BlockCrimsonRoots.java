package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonRoots extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonRoots() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Roots";
    }
}