package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement109 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_109");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement109() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement109(BlockState blockstate) {
        super(blockstate);
    }
}