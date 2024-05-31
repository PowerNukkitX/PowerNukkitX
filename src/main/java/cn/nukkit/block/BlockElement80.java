package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement80 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_80");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement80() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement80(BlockState blockstate) {
        super(blockstate);
    }
}