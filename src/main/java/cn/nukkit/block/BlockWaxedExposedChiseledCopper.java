package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedChiseledCopper extends Block {
    public static final BlockProperties $1 = new BlockProperties(WAXED_EXPOSED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}