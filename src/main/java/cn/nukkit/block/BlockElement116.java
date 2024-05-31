package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement116 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_116");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement116() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement116(BlockState blockstate) {
        super(blockstate);
    }
}