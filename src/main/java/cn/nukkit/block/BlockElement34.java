package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement34 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_34");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement34() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement34(BlockState blockstate) {
        super(blockstate);
    }
}