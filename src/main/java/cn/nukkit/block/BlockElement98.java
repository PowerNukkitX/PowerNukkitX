package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement98 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_98");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement98() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement98(BlockState blockstate) {
        super(blockstate);
    }
}