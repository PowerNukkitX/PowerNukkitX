package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement40 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_40");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement40() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement40(BlockState blockstate) {
        super(blockstate);
    }
}