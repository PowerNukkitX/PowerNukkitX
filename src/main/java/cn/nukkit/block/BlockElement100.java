package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement100 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_100");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement100() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement100(BlockState blockstate) {
        super(blockstate);
    }
}