package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedNylium extends BlockNylium {
    public static final BlockProperties $1 = new BlockProperties(WARPED_NYLIUM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedNylium() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedNylium(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Warped Nylium";
    }
}