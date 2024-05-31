package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement32 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_32");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement32() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement32(BlockState blockstate) {
        super(blockstate);
    }
}