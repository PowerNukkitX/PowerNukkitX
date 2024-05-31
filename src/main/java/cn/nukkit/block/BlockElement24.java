package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement24 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_24");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement24() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement24(BlockState blockstate) {
        super(blockstate);
    }
}