package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement112 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_112");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement112() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement112(BlockState blockstate) {
        super(blockstate);
    }
}