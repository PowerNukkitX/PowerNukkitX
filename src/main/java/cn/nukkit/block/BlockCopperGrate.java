package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}