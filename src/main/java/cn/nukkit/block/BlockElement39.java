package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement39 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_39");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement39() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement39(BlockState blockstate) {
        super(blockstate);
    }
}