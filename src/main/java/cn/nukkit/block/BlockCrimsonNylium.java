package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonNylium extends BlockNylium {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_NYLIUM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonNylium() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonNylium(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Nylium";
    }
}