package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement16 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_16");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement16() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement16(BlockState blockstate) {
        super(blockstate);
    }
}