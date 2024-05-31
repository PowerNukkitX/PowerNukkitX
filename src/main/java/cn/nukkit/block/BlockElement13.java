package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement13 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_13");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement13() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement13(BlockState blockstate) {
        super(blockstate);
    }
}