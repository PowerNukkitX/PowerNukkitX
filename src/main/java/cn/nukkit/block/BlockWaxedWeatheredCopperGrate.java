package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(WAXED_WEATHERED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedWeatheredCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}