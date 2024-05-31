package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement101 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_101");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement101() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement101(BlockState blockstate) {
        super(blockstate);
    }
}