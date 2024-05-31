package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement108 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_108");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement108() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement108(BlockState blockstate) {
        super(blockstate);
    }
}