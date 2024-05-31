package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockReserved6 extends Block {
    public static final BlockProperties $1 = new BlockProperties(RESERVED6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockReserved6() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockReserved6(BlockState blockstate) {
        super(blockstate);
    }
}