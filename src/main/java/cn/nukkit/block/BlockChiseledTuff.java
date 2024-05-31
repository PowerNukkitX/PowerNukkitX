package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuff extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_TUFF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledTuff() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledTuff(BlockState blockstate) {
        super(blockstate);
    }
}