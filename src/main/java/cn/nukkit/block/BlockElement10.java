package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement10 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_10");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement10() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement10(BlockState blockstate) {
        super(blockstate);
    }
}