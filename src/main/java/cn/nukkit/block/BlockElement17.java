package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement17 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_17");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement17() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement17(BlockState blockstate) {
        super(blockstate);
    }
}