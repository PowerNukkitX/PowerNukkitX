package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(WAXED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}