package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperGrate extends Block {
    public static final BlockProperties $1 = new BlockProperties(OXIDIZED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOxidizedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOxidizedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }
}