package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement46 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_46");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement46() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement46(BlockState blockstate) {
        super(blockstate);
    }
}