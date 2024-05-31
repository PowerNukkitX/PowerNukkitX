package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement8 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_8");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement8() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement8(BlockState blockstate) {
        super(blockstate);
    }
}