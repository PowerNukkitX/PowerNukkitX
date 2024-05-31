package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockExposedChiseledCopper extends Block {
    public static final BlockProperties $1 = new BlockProperties(EXPOSED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}