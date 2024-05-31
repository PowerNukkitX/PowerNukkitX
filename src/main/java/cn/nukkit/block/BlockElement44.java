package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement44 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_44");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement44() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement44(BlockState blockstate) {
        super(blockstate);
    }
}