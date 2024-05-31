package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement57 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_57");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement57() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement57(BlockState blockstate) {
        super(blockstate);
    }
}