package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement106 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_106");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement106() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement106(BlockState blockstate) {
        super(blockstate);
    }
}