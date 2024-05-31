package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(EXPOSED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}