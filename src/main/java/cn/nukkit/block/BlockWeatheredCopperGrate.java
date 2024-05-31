package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(WEATHERED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWeatheredCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWeatheredCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}