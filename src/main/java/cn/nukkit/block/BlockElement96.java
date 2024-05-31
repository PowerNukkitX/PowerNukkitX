package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement96 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_96");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement96() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement96(BlockState blockstate) {
        super(blockstate);
    }
}