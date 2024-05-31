package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement66 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_66");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement66() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement66(BlockState blockstate) {
        super(blockstate);
    }
}