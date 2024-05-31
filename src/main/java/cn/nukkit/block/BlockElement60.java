package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement60 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_60");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement60() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement60(BlockState blockstate) {
        super(blockstate);
    }
}