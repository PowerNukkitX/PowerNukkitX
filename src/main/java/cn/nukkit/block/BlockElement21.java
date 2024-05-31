package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement21 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_21");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement21() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement21(BlockState blockstate) {
        super(blockstate);
    }
}