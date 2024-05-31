package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledCopper extends Block {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }
}