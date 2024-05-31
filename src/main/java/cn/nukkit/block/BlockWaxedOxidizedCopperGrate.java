package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(WAXED_OXIDIZED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}