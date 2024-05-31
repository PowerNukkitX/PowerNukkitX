package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxidizedChiseledCopper extends Block {
    public static final BlockProperties $1 = new BlockProperties(OXIDIZED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}