package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWaxedChiseledCopper extends Block {
    public static final BlockProperties $1 = new BlockProperties(WAXED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWaxedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}