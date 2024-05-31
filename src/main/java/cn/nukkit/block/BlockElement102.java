package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement102 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_102");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement102() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement102(BlockState blockstate) {
        super(blockstate);
    }
}