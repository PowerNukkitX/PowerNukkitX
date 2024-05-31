package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement61 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_61");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement61() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement61(BlockState blockstate) {
        super(blockstate);
    }
}